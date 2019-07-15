"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = {
    test(value) {
        return value && value.kind === 'QueryPlan';
    },
    serialize(queryPlan, config, indentation, depth, refs, printer) {
        return ('QueryPlan {' +
            printNodes(queryPlan.node ? [queryPlan.node] : undefined, config, indentation, depth, refs, printer) +
            '}');
    },
};
function printNode(node, config, indentation, depth, refs, printer) {
    let result = '';
    const indentationNext = indentation + config.indent;
    switch (node.kind) {
        case 'Fetch':
            result +=
                `Fetch(service: "${node.serviceName}")` +
                    ' {' +
                    config.spacingOuter +
                    indentationNext +
                    (node.requires
                        ? printer(node.requires, config, indentationNext, depth, refs, printer) +
                            ' =>' +
                            config.spacingOuter +
                            indentationNext
                        : '') +
                    printer(node.selectionSet, config, indentationNext, depth, refs, printer) +
                    config.spacingOuter +
                    indentation +
                    '}';
            break;
        case 'Flatten':
            result += `Flatten(path: "${node.path.join('.')}")`;
            break;
        default:
            result += node.kind;
    }
    const nodes = 'nodes' in node ? node.nodes : 'node' in node ? [node.node] : [];
    if (nodes.length > 0) {
        result +=
            ' {' + printNodes(nodes, config, indentation, depth, refs, printer) + '}';
    }
    return result;
}
function printNodes(nodes, config, indentation, depth, refs, printer) {
    let result = '';
    if (nodes && nodes.length > 0) {
        result += config.spacingOuter;
        const indentationNext = indentation + config.indent;
        for (let i = 0; i < nodes.length; i++) {
            const node = nodes[i];
            if (!node)
                continue;
            result +=
                indentationNext +
                    printNode(node, config, indentationNext, depth, refs, printer);
            if (i < nodes.length - 1) {
                result += ',' + config.spacingInner;
            }
            else if (!config.min) {
                result += ',';
            }
        }
        result += config.spacingOuter + indentation;
    }
    return result;
}
//# sourceMappingURL=queryPlanSerializer.js.map