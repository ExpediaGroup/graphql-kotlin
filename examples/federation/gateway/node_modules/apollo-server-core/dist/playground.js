"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const playgroundVersion = '1.7.31';
exports.defaultPlaygroundOptions = {
    version: playgroundVersion,
    settings: {
        'general.betaUpdates': false,
        'editor.theme': 'dark',
        'editor.cursorShape': 'line',
        'editor.reuseHeaders': true,
        'tracing.hideTracingResponse': true,
        'queryPlan.hideQueryPlanResponse': true,
        'editor.fontSize': 14,
        'editor.fontFamily': `'Source Code Pro', 'Consolas', 'Inconsolata', 'Droid Sans Mono', 'Monaco', monospace`,
        'request.credentials': 'omit',
    },
};
function createPlaygroundOptions(playground) {
    const isDev = process.env.NODE_ENV !== 'production';
    const enabled = typeof playground !== 'undefined' ? !!playground : isDev;
    if (!enabled) {
        return undefined;
    }
    const playgroundOverrides = typeof playground === 'boolean' ? {} : playground || {};
    const settingsOverrides = playgroundOverrides.hasOwnProperty('settings')
        ? {
            settings: Object.assign(Object.assign({}, exports.defaultPlaygroundOptions.settings), playgroundOverrides.settings),
        }
        : { settings: undefined };
    const playgroundOptions = Object.assign(Object.assign(Object.assign({}, exports.defaultPlaygroundOptions), playgroundOverrides), settingsOverrides);
    return playgroundOptions;
}
exports.createPlaygroundOptions = createPlaygroundOptions;
//# sourceMappingURL=playground.js.map