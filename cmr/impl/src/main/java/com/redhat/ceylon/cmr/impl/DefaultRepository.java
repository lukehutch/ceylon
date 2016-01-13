/*
 * Copyright 2011 Red Hat inc. and third party contributors as noted 
 * by the author tags.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.ceylon.cmr.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.CmrRepository;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.spi.Node;
import com.redhat.ceylon.cmr.spi.OpenNode;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.cmr.RepositoryException;

/**
 * Default.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultRepository extends AbstractRepository {

    public DefaultRepository(OpenNode root) {
        super(root);
    }

    protected ArtifactResult getArtifactResultInternal(RepositoryManager manager, Node node) {
        return new DefaultArtifactResult(this, manager, node);
    }

    protected static class DefaultArtifactResult extends AbstractCeylonArtifactResult {

        private Node node;

        protected DefaultArtifactResult(CmrRepository repository, RepositoryManager manager, Node node) {
            this(repository, manager, node, ArtifactContext.fromNode(node));
            this.node = node;
        }
        
        private DefaultArtifactResult(CmrRepository repository, RepositoryManager manager, Node node, ArtifactContext ac) {
            super(repository, manager, ac.getName(), ac.getVersion(), ac.isNoDistOverrides());
            this.node = node;
        }

        protected File artifactInternal() throws RepositoryException {
            try {
                return node.getContent(File.class);
            } catch (IOException e) {
                throw new RepositoryException(e);
            }
        }
        
        @Override
        public String repositoryDisplayString() {
            String repositoryDisplayString = NodeUtils.getRepositoryDisplayString(node);
            File artifact = artifact();
            File originFile = new File(artifact.getParentFile(), artifact.getName() + RootRepositoryManager.ORIGIN);
            if (originFile.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(originFile));
                    try {
                        String line = reader.readLine();
                        if (line != null && !line.trim().isEmpty()) {
                            repositoryDisplayString = line;
                        }
                    } finally {
                        reader.close();
                    }
                } catch(IOException e) {
                } 
            }
            return repositoryDisplayString;
        }
    }

    @Override
    public boolean isMaven() {
        return false;
    }
}
