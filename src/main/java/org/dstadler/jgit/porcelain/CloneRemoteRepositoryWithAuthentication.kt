package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.errors.UnsupportedCredentialItem
import org.eclipse.jgit.transport.CredentialItem
import org.eclipse.jgit.transport.CredentialItem.YesNoType
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish
import java.io.File
import java.io.IOException

/*
   Copyright 2015 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */ /**
 * Simple snippet which shows how to clone a repository from a remote source
 * via ssh protocol and username/password authentication.
 *
 * @author dominik.stadler at gmx.at
 */
object CloneRemoteRepositoryWithAuthentication {
	private const val REMOTE_URL = "ssh://<user>:<pwd>@<host>:22/<path-to-remote-repo>/"

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// this is necessary when the remote host does not have a valid certificate, ideally we would install the certificate in the JVM
		// instead of this unsecure workaround!
		val allowHosts: CredentialsProvider = object : CredentialsProvider() {
			override fun supports(vararg items: CredentialItem): Boolean {
				for (item in items) {
					if (item is YesNoType) {
						return true
					}
				}
				return false
			}

			@Throws(UnsupportedCredentialItem::class)
			override fun get(uri: URIish, vararg items: CredentialItem): Boolean {
				for (item in items) {
					if (item is YesNoType) {
						item.value = true
						return true
					}
				}
				return false
			}

			override fun isInteractive(): Boolean {
				return false
			}
		}

		// prepare a new folder for the cloned repository
		val localPath = File.createTempFile("TestGitRepository", "")
		if (!localPath.delete()) {
			throw IOException("Could not delete temporary file $localPath")
		}

		// then clone
		println("Cloning from $REMOTE_URL to $localPath")
		Git.cloneRepository()
				.setURI(REMOTE_URL)
				.setDirectory(localPath)
				.setCredentialsProvider(allowHosts)
				.call().use { result ->
					// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
					println("Having repository: " + result.repository.directory)
				}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}