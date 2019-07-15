var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var graphql_1 = require("graphql");
var values_1 = require("graphql/execution/values");
var hasOwn = Object.prototype.hasOwnProperty;
// Abstract base class of any visitor implementation, defining the available
// visitor methods along with their parameter types, and providing a static
// helper function for determining whether a subclass implements a given
// visitor method, as opposed to inheriting one of the stubs defined here.
var SchemaVisitor = /** @class */ (function () {
    function SchemaVisitor() {
    }
    // Determine if this SchemaVisitor (sub)class implements a particular
    // visitor method.
    SchemaVisitor.implementsVisitorMethod = function (methodName) {
        if (!methodName.startsWith('visit')) {
            return false;
        }
        var method = this.prototype[methodName];
        if (typeof method !== 'function') {
            return false;
        }
        if (this === SchemaVisitor) {
            // The SchemaVisitor class implements every visitor method.
            return true;
        }
        var stub = SchemaVisitor.prototype[methodName];
        if (method === stub) {
            // If this.prototype[methodName] was just inherited from SchemaVisitor,
            // then this class does not really implement the method.
            return false;
        }
        return true;
    };
    // Concrete subclasses of SchemaVisitor should override one or more of these
    // visitor methods, in order to express their interest in handling certain
    // schema types/locations. Each method may return null to remove the given
    // type from the schema, a non-null value of the same type to update the
    // type in the schema, or nothing to leave the type as it was.
    /* tslint:disable:no-empty */
    SchemaVisitor.prototype.visitSchema = function (schema) { };
    SchemaVisitor.prototype.visitScalar = function (scalar) { };
    SchemaVisitor.prototype.visitObject = function (object) { };
    SchemaVisitor.prototype.visitFieldDefinition = function (field, details) { };
    SchemaVisitor.prototype.visitArgumentDefinition = function (argument, details) { };
    SchemaVisitor.prototype.visitInterface = function (iface) { };
    SchemaVisitor.prototype.visitUnion = function (union) { };
    SchemaVisitor.prototype.visitEnum = function (type) { };
    SchemaVisitor.prototype.visitEnumValue = function (value, details) { };
    SchemaVisitor.prototype.visitInputObject = function (object) { };
    SchemaVisitor.prototype.visitInputFieldDefinition = function (field, details) { };
    return SchemaVisitor;
}());
exports.SchemaVisitor = SchemaVisitor;
// Generic function for visiting GraphQLSchema objects.
function visitSchema(schema, 
// To accommodate as many different visitor patterns as possible, the
// visitSchema function does not simply accept a single instance of the
// SchemaVisitor class, but instead accepts a function that takes the
// current VisitableSchemaType object and the name of a visitor method and
// returns an array of SchemaVisitor instances that implement the visitor
// method and have an interest in handling the given VisitableSchemaType
// object. In the simplest case, this function can always return an array
// containing a single visitor object, without even looking at the type or
// methodName parameters. In other cases, this function might sometimes
// return an empty array to indicate there are no visitors that should be
// applied to the given VisitableSchemaType object. For an example of a
// visitor pattern that benefits from this abstraction, see the
// SchemaDirectiveVisitor class below.
visitorSelector) {
    // Helper function that calls visitorSelector and applies the resulting
    // visitors to the given type, with arguments [type, ...args].
    function callMethod(methodName, type) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        visitorSelector(type, methodName).every(function (visitor) {
            var newType = visitor[methodName].apply(visitor, [type].concat(args));
            if (typeof newType === 'undefined') {
                // Keep going without modifying type.
                return true;
            }
            if (methodName === 'visitSchema' ||
                type instanceof graphql_1.GraphQLSchema) {
                throw new Error("Method " + methodName + " cannot replace schema with " + newType);
            }
            if (newType === null) {
                // Stop the loop and return null form callMethod, which will cause
                // the type to be removed from the schema.
                type = null;
                return false;
            }
            // Update type to the new type returned by the visitor method, so that
            // later directives will see the new type, and callMethod will return
            // the final type.
            type = newType;
            return true;
        });
        // If there were no directives for this type object, or if all visitor
        // methods returned nothing, type will be returned unmodified.
        return type;
    }
    // Recursive helper function that calls any appropriate visitor methods for
    // each object in the schema, then traverses the object's children (if any).
    function visit(type) {
        if (type instanceof graphql_1.GraphQLSchema) {
            // Unlike the other types, the root GraphQLSchema object cannot be
            // replaced by visitor methods, because that would make life very hard
            // for SchemaVisitor subclasses that rely on the original schema object.
            callMethod('visitSchema', type);
            updateEachKey(type.getTypeMap(), function (namedType, typeName) {
                if (!typeName.startsWith('__')) {
                    // Call visit recursively to let it determine which concrete
                    // subclass of GraphQLNamedType we found in the type map. Because
                    // we're using updateEachKey, the result of visit(namedType) may
                    // cause the type to be removed or replaced.
                    return visit(namedType);
                }
            });
            return type;
        }
        if (type instanceof graphql_1.GraphQLObjectType) {
            // Note that callMethod('visitObject', type) may not actually call any
            // methods, if there are no @directive annotations associated with this
            // type, or if this SchemaDirectiveVisitor subclass does not override
            // the visitObject method.
            var newObject = callMethod('visitObject', type);
            if (newObject) {
                visitFields(newObject);
            }
            return newObject;
        }
        if (type instanceof graphql_1.GraphQLInterfaceType) {
            var newInterface = callMethod('visitInterface', type);
            if (newInterface) {
                visitFields(newInterface);
            }
            return newInterface;
        }
        if (type instanceof graphql_1.GraphQLInputObjectType) {
            var newInputObject_1 = callMethod('visitInputObject', type);
            if (newInputObject_1) {
                updateEachKey(newInputObject_1.getFields(), function (field) {
                    // Since we call a different method for input object fields, we
                    // can't reuse the visitFields function here.
                    return callMethod('visitInputFieldDefinition', field, {
                        objectType: newInputObject_1,
                    });
                });
            }
            return newInputObject_1;
        }
        if (type instanceof graphql_1.GraphQLScalarType) {
            return callMethod('visitScalar', type);
        }
        if (type instanceof graphql_1.GraphQLUnionType) {
            return callMethod('visitUnion', type);
        }
        if (type instanceof graphql_1.GraphQLEnumType) {
            var newEnum_1 = callMethod('visitEnum', type);
            if (newEnum_1) {
                updateEachKey(newEnum_1.getValues(), function (value) {
                    return callMethod('visitEnumValue', value, {
                        enumType: newEnum_1,
                    });
                });
            }
            return newEnum_1;
        }
        throw new Error("Unexpected schema type: " + type);
    }
    function visitFields(type) {
        updateEachKey(type.getFields(), function (field) {
            // It would be nice if we could call visit(field) recursively here, but
            // GraphQLField is merely a type, not a value that can be detected using
            // an instanceof check, so we have to visit the fields in this lexical
            // context, so that TypeScript can validate the call to
            // visitFieldDefinition.
            var newField = callMethod('visitFieldDefinition', field, {
                // While any field visitor needs a reference to the field object, some
                // field visitors may also need to know the enclosing (parent) type,
                // perhaps to determine if the parent is a GraphQLObjectType or a
                // GraphQLInterfaceType. To obtain a reference to the parent, a
                // visitor method can have a second parameter, which will be an object
                // with an .objectType property referring to the parent.
                objectType: type,
            });
            if (newField && newField.args) {
                updateEachKey(newField.args, function (arg) {
                    return callMethod('visitArgumentDefinition', arg, {
                        // Like visitFieldDefinition, visitArgumentDefinition takes a
                        // second parameter that provides additional context, namely the
                        // parent .field and grandparent .objectType. Remember that the
                        // current GraphQLSchema is always available via this.schema.
                        field: newField,
                        objectType: type,
                    });
                });
            }
            return newField;
        });
    }
    visit(schema);
    // Return the original schema for convenience, even though it cannot have
    // been replaced or removed by the code above.
    return schema;
}
exports.visitSchema = visitSchema;
// Update any references to named schema types that disagree with the named
// types found in schema.getTypeMap().
function healSchema(schema) {
    heal(schema);
    return schema;
    function heal(type) {
        if (type instanceof graphql_1.GraphQLSchema) {
            var originalTypeMap_1 = type.getTypeMap();
            var actualNamedTypeMap_1 = Object.create(null);
            // If any of the .name properties of the GraphQLNamedType objects in
            // schema.getTypeMap() have changed, the keys of the type map need to
            // be updated accordingly.
            each(originalTypeMap_1, function (namedType, typeName) {
                if (typeName.startsWith('__')) {
                    return;
                }
                var actualName = namedType.name;
                if (actualName.startsWith('__')) {
                    return;
                }
                if (hasOwn.call(actualNamedTypeMap_1, actualName)) {
                    throw new Error("Duplicate schema type name " + actualName);
                }
                actualNamedTypeMap_1[actualName] = namedType;
                // Note: we are deliberately leaving namedType in the schema by its
                // original name (which might be different from actualName), so that
                // references by that name can be healed.
            });
            // Now add back every named type by its actual name.
            each(actualNamedTypeMap_1, function (namedType, typeName) {
                originalTypeMap_1[typeName] = namedType;
            });
            // Directive declaration argument types can refer to named types.
            each(type.getDirectives(), function (decl) {
                if (decl.args) {
                    each(decl.args, function (arg) {
                        arg.type = healType(arg.type);
                    });
                }
            });
            each(originalTypeMap_1, function (namedType, typeName) {
                if (!typeName.startsWith('__')) {
                    heal(namedType);
                }
            });
            updateEachKey(originalTypeMap_1, function (namedType, typeName) {
                // Dangling references to renamed types should remain in the schema
                // during healing, but must be removed now, so that the following
                // invariant holds for all names: schema.getType(name).name === name
                if (!typeName.startsWith('__') &&
                    !hasOwn.call(actualNamedTypeMap_1, typeName)) {
                    return null;
                }
            });
        }
        else if (type instanceof graphql_1.GraphQLObjectType) {
            healFields(type);
            each(type.getInterfaces(), function (iface) { return heal(iface); });
        }
        else if (type instanceof graphql_1.GraphQLInterfaceType) {
            healFields(type);
        }
        else if (type instanceof graphql_1.GraphQLInputObjectType) {
            each(type.getFields(), function (field) {
                field.type = healType(field.type);
            });
        }
        else if (type instanceof graphql_1.GraphQLScalarType) {
            // Nothing to do.
        }
        else if (type instanceof graphql_1.GraphQLUnionType) {
            updateEachKey(type.getTypes(), function (t) { return healType(t); });
        }
        else if (type instanceof graphql_1.GraphQLEnumType) {
            // Nothing to do.
        }
        else {
            throw new Error("Unexpected schema type: " + type);
        }
    }
    function healFields(type) {
        each(type.getFields(), function (field) {
            field.type = healType(field.type);
            if (field.args) {
                each(field.args, function (arg) {
                    arg.type = healType(arg.type);
                });
            }
        });
    }
    function healType(type) {
        // Unwrap the two known wrapper types
        if (type instanceof graphql_1.GraphQLList) {
            type = new graphql_1.GraphQLList(healType(type.ofType));
        }
        else if (type instanceof graphql_1.GraphQLNonNull) {
            type = new graphql_1.GraphQLNonNull(healType(type.ofType));
        }
        else if (graphql_1.isNamedType(type)) {
            // If a type annotation on a field or an argument or a union member is
            // any `GraphQLNamedType` with a `name`, then it must end up identical
            // to `schema.getType(name)`, since `schema.getTypeMap()` is the source
            // of truth for all named schema types.
            var namedType = type;
            var officialType = schema.getType(namedType.name);
            if (officialType && namedType !== officialType) {
                return officialType;
            }
        }
        return type;
    }
}
exports.healSchema = healSchema;
// This class represents a reusable implementation of a @directive that may
// appear in a GraphQL schema written in Schema Definition Language.
//
// By overriding one or more visit{Object,Union,...} methods, a subclass
// registers interest in certain schema types, such as GraphQLObjectType,
// GraphQLUnionType, etc. When SchemaDirectiveVisitor.visitSchemaDirectives is
// called with a GraphQLSchema object and a map of visitor subclasses, the
// overidden methods of those subclasses allow the visitors to obtain
// references to any type objects that have @directives attached to them,
// enabling visitors to inspect or modify the schema as appropriate.
//
// For example, if a directive called @rest(url: "...") appears after a field
// definition, a SchemaDirectiveVisitor subclass could provide meaning to that
// directive by overriding the visitFieldDefinition method (which receives a
// GraphQLField parameter), and then the body of that visitor method could
// manipulate the field's resolver function to fetch data from a REST endpoint
// described by the url argument passed to the @rest directive:
//
//   const typeDefs = `
//   type Query {
//     people: [Person] @rest(url: "/api/v1/people")
//   }`;
//
//   const schema = makeExecutableSchema({ typeDefs });
//
//   SchemaDirectiveVisitor.visitSchemaDirectives(schema, {
//     rest: class extends SchemaDirectiveVisitor {
//       public visitFieldDefinition(field: GraphQLField<any, any>) {
//         const { url } = this.args;
//         field.resolve = () => fetch(url);
//       }
//     }
//   });
//
// The subclass in this example is defined as an anonymous class expression,
// for brevity. A truly reusable SchemaDirectiveVisitor would most likely be
// defined in a library using a named class declaration, and then exported for
// consumption by other modules and packages.
//
// See below for a complete list of overridable visitor methods, their
// parameter types, and more details about the properties exposed by instances
// of the SchemaDirectiveVisitor class.
var SchemaDirectiveVisitor = /** @class */ (function (_super) {
    __extends(SchemaDirectiveVisitor, _super);
    // Mark the constructor protected to enforce passing SchemaDirectiveVisitor
    // subclasses (not instances) to visitSchemaDirectives.
    function SchemaDirectiveVisitor(config) {
        var _this = _super.call(this) || this;
        _this.name = config.name;
        _this.args = config.args;
        _this.visitedType = config.visitedType;
        _this.schema = config.schema;
        _this.context = config.context;
        return _this;
    }
    // Override this method to return a custom GraphQLDirective (or modify one
    // already present in the schema) to enforce argument types, provide default
    // argument values, or specify schema locations where this @directive may
    // appear. By default, any declaration found in the schema will be returned.
    SchemaDirectiveVisitor.getDirectiveDeclaration = function (directiveName, schema) {
        return schema.getDirective(directiveName);
    };
    // Call SchemaDirectiveVisitor.visitSchemaDirectives to visit every
    // @directive in the schema and create an appropriate SchemaDirectiveVisitor
    // instance to visit the object decorated by the @directive.
    SchemaDirectiveVisitor.visitSchemaDirectives = function (schema, directiveVisitors, 
    // Optional context object that will be available to all visitor instances
    // via this.context. Defaults to an empty null-prototype object.
    context) {
        if (context === void 0) { 
        // Optional context object that will be available to all visitor instances
        // via this.context. Defaults to an empty null-prototype object.
        context = Object.create(null); }
        // If the schema declares any directives for public consumption, record
        // them here so that we can properly coerce arguments when/if we encounter
        // an occurrence of the directive while walking the schema below.
        var declaredDirectives = this.getDeclaredDirectives(schema, directiveVisitors);
        // Map from directive names to lists of SchemaDirectiveVisitor instances
        // created while visiting the schema.
        var createdVisitors = Object.create(null);
        Object.keys(directiveVisitors).forEach(function (directiveName) {
            createdVisitors[directiveName] = [];
        });
        function visitorSelector(type, methodName) {
            var visitors = [];
            var directiveNodes = type.astNode && type.astNode.directives;
            if (!directiveNodes) {
                return visitors;
            }
            directiveNodes.forEach(function (directiveNode) {
                var directiveName = directiveNode.name.value;
                if (!hasOwn.call(directiveVisitors, directiveName)) {
                    return;
                }
                var visitorClass = directiveVisitors[directiveName];
                // Avoid creating visitor objects if visitorClass does not override
                // the visitor method named by methodName.
                if (!visitorClass.implementsVisitorMethod(methodName)) {
                    return;
                }
                var decl = declaredDirectives[directiveName];
                var args;
                if (decl) {
                    // If this directive was explicitly declared, use the declared
                    // argument types (and any default values) to check, coerce, and/or
                    // supply default values for the given arguments.
                    args = values_1.getArgumentValues(decl, directiveNode);
                }
                else {
                    // If this directive was not explicitly declared, just convert the
                    // argument nodes to their corresponding JavaScript values.
                    args = Object.create(null);
                    directiveNode.arguments.forEach(function (arg) {
                        args[arg.name.value] = valueFromASTUntyped(arg.value);
                    });
                }
                // As foretold in comments near the top of the visitSchemaDirectives
                // method, this is where instances of the SchemaDirectiveVisitor class
                // get created and assigned names. While subclasses could override the
                // constructor method, the constructor is marked as protected, so
                // these are the only arguments that will ever be passed.
                visitors.push(new visitorClass({
                    name: directiveName,
                    args: args,
                    visitedType: type,
                    schema: schema,
                    context: context,
                }));
            });
            if (visitors.length > 0) {
                visitors.forEach(function (visitor) {
                    createdVisitors[visitor.name].push(visitor);
                });
            }
            return visitors;
        }
        visitSchema(schema, visitorSelector);
        // Automatically update any references to named schema types replaced
        // during the traversal, so implementors don't have to worry about that.
        healSchema(schema);
        return createdVisitors;
    };
    SchemaDirectiveVisitor.getDeclaredDirectives = function (schema, directiveVisitors) {
        var declaredDirectives = Object.create(null);
        each(schema.getDirectives(), function (decl) {
            declaredDirectives[decl.name] = decl;
        });
        // If the visitor subclass overrides getDirectiveDeclaration, and it
        // returns a non-null GraphQLDirective, use that instead of any directive
        // declared in the schema itself. Reasoning: if a SchemaDirectiveVisitor
        // goes to the trouble of implementing getDirectiveDeclaration, it should
        // be able to rely on that implementation.
        each(directiveVisitors, function (visitorClass, directiveName) {
            var decl = visitorClass.getDirectiveDeclaration(directiveName, schema);
            if (decl) {
                declaredDirectives[directiveName] = decl;
            }
        });
        each(declaredDirectives, function (decl, name) {
            if (!hasOwn.call(directiveVisitors, name)) {
                // SchemaDirectiveVisitors.visitSchemaDirectives might be called
                // multiple times with partial directiveVisitors maps, so it's not
                // necessarily an error for directiveVisitors to be missing an
                // implementation of a directive that was declared in the schema.
                return;
            }
            var visitorClass = directiveVisitors[name];
            each(decl.locations, function (loc) {
                var visitorMethodName = directiveLocationToVisitorMethodName(loc);
                if (SchemaVisitor.implementsVisitorMethod(visitorMethodName) &&
                    !visitorClass.implementsVisitorMethod(visitorMethodName)) {
                    // While visitor subclasses may implement extra visitor methods,
                    // it's definitely a mistake if the GraphQLDirective declares itself
                    // applicable to certain schema locations, and the visitor subclass
                    // does not implement all the corresponding methods.
                    throw new Error("SchemaDirectiveVisitor for @" + name + " must implement " + visitorMethodName + " method");
                }
            });
        });
        return declaredDirectives;
    };
    return SchemaDirectiveVisitor;
}(SchemaVisitor));
exports.SchemaDirectiveVisitor = SchemaDirectiveVisitor;
// Convert a string like "FIELD_DEFINITION" to "visitFieldDefinition".
function directiveLocationToVisitorMethodName(loc) {
    return 'visit' + loc.replace(/([^_]*)_?/g, function (wholeMatch, part) {
        return part.charAt(0).toUpperCase() + part.slice(1).toLowerCase();
    });
}
function each(arrayOrObject, callback) {
    Object.keys(arrayOrObject).forEach(function (key) {
        callback(arrayOrObject[key], key);
    });
}
// A more powerful version of each that has the ability to replace or remove
// array or object keys.
function updateEachKey(arrayOrObject, 
// The callback can return nothing to leave the key untouched, null to remove
// the key from the array or object, or a non-null V to replace the value.
callback) {
    var deletedCount = 0;
    Object.keys(arrayOrObject).forEach(function (key) {
        var result = callback(arrayOrObject[key], key);
        if (typeof result === 'undefined') {
            return;
        }
        if (result === null) {
            delete arrayOrObject[key];
            deletedCount++;
            return;
        }
        arrayOrObject[key] = result;
    });
    if (deletedCount > 0 && Array.isArray(arrayOrObject)) {
        // Remove any holes from the array due to deleted elements.
        arrayOrObject.splice(0).forEach(function (elem) {
            arrayOrObject.push(elem);
        });
    }
}
// Similar to the graphql-js function of the same name, slightly simplified:
// https://github.com/graphql/graphql-js/blob/master/src/utilities/valueFromASTUntyped.js
function valueFromASTUntyped(valueNode) {
    switch (valueNode.kind) {
        case graphql_1.Kind.NULL:
            return null;
        case graphql_1.Kind.INT:
            return parseInt(valueNode.value, 10);
        case graphql_1.Kind.FLOAT:
            return parseFloat(valueNode.value);
        case graphql_1.Kind.STRING:
        case graphql_1.Kind.ENUM:
        case graphql_1.Kind.BOOLEAN:
            return valueNode.value;
        case graphql_1.Kind.LIST:
            return valueNode.values.map(valueFromASTUntyped);
        case graphql_1.Kind.OBJECT:
            var obj_1 = Object.create(null);
            valueNode.fields.forEach(function (field) {
                obj_1[field.name.value] = valueFromASTUntyped(field.value);
            });
            return obj_1;
        /* istanbul ignore next */
        default:
            throw new Error('Unexpected value kind: ' + valueNode.kind);
    }
}
//# sourceMappingURL=schemaVisitor.js.map