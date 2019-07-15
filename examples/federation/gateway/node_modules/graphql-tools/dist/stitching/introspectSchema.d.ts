import { GraphQLSchema } from 'graphql';
import { ApolloLink } from 'apollo-link';
import { Fetcher } from './makeRemoteExecutableSchema';
export default function introspectSchema(fetcher: ApolloLink | Fetcher, linkContext?: {
    [key: string]: any;
}): Promise<GraphQLSchema>;
