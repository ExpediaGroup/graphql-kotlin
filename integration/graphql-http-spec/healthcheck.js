let { fetch } = require('@whatwg-node/fetch')

const targetUrl = 'http://localhost:8080/graphql';

async function healthCheck() {
  let attempts = 100;
  while (attempts--) {
    console.log("waiting for server");
    try {
      const health = await fetch(targetUrl, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          query: 'query { __typename }',
        })
      })
      if (health.status === 200) {
        return true;
      } else {
          console.log(`Error status: ${health.status}`);
      }

    } catch (e) {
      console.log(`Error: ${e}`);
    }

    await new Promise((r) => setTimeout(r, 1000));
  }

  return false;
}

healthCheck().then((successful) => {
  if (!successful) {
    throw new Error('failed to start');
  }
});
