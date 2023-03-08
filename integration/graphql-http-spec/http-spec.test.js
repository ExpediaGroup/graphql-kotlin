let { fetch } = require('@whatwg-node/fetch')
let { serverAudits } = require('graphql-http')

const targetUrl = 'http://localhost:8080/graphql';
// verify server is up
let attempts = 10;
while (attempts--) {
  try {
    const healthCheck = await fetch(targetUrl, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        query: 'query { __typename }',
      })
    })

    if (healthCheck.data?.__typename) {
        break;
    }
  } catch (e) {
    // continue
  }
}

// compliance tests
for (const audit of serverAudits({
  url: targetUrl,
  fetchFn: fetch,
})) {
  test(audit.name, async () => {
    const result = await audit.fn();
    if (result.status === 'error') {
      throw result.reason;
    }
    if (result.status === 'warn') {
      console.warn('WARN: ' + audit.name + '\nREASON: ' + result.reason);
      // or throw if you want full compliance (warnings are not requirements)
//      throw result.reason;
    }
    // result.status === 'ok'
  });
}
