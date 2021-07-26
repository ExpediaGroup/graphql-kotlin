/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
import React from 'react';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Link from '@docusaurus/Link';
import {useActivePlugin, useActiveVersion, useDocVersionSuggestions} from '@theme/hooks/useDocs';
import {useDocsPreferredVersion} from '@docusaurus/theme-common';

const getVersionMainDoc = version => version.docs.find(doc => doc.id === version.mainDocId);

function DocVersionSuggestions() {
  const {
    siteConfig: {
      title: siteTitle
    }
  } = useDocusaurusContext();
  const {
    pluginId
  } = useActivePlugin({
    failfast: true
  });
  const {
    savePreferredVersionName
  } = useDocsPreferredVersion(pluginId);
  const activeVersion = useActiveVersion(pluginId);
  const {
    latestDocSuggestion,
    latestVersionSuggestion
  } = useDocVersionSuggestions(pluginId); // No suggestion to be made

  if (!latestVersionSuggestion) {
    return <></>;
  } // try to link to same doc in latest version (not always possible)
  // fallback to main doc of latest version


  const latestVersionSuggestedDoc = latestDocSuggestion ?? getVersionMainDoc(latestVersionSuggestion);

  if (activeVersion.name === 'current') {
    return (
      <div className="alert alert--warning margin-bottom--md" role="alert">
        <div>
          This documentation is for an unreleased version.
        </div>
        <div className="margin-top--md">
          For the latest documentation, see the{' '}
          <strong>
            <Link to={latestVersionSuggestedDoc.path} onClick={() => savePreferredVersionName(latestVersionSuggestion.name)}>
              latest version
            </Link>
          </strong>{' '}
          ({latestVersionSuggestion.label}).
        </div>
      </div>
    );
  } else {
    return <div/>;
  }
}

export default DocVersionSuggestions;
