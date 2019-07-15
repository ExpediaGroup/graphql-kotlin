import astSerializer from './astSerializer';
import selectionSetSerializer from './selectionSetSerializer';
import typeSerializer from './typeSerializer';
import queryPlanSerializer from './queryPlanSerializer';
export { astSerializer, selectionSetSerializer, typeSerializer, queryPlanSerializer, };
declare global {
    namespace jest {
        interface Expect {
            addSnapshotSerializer(serializer: import('pretty-format').Plugin): void;
        }
    }
}
//# sourceMappingURL=index.d.ts.map