package org.dstadler.jgit.porcelain

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import java.io.IOException

/**
 * Copyright 2019 Vasyl Khrystiuk https://github.com/msangel
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object CloneRemoteRepositoryIntoMemoryAndReadFile {
	private const val REMOTE_URL = "https://github.com/github/testrepo.git"
	private const val BRANCH = "master"
	private const val FILE_TO_READ = "test/alias.c"

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val repoDesc = DfsRepositoryDescription()
		val repo = InMemoryRepository(repoDesc)
		val git = Git(repo)
		git.fetch()
				.setRemote(REMOTE_URL)
				.setRefSpecs(RefSpec("+refs/heads/*:refs/heads/*"))
				.call()
		repo.objectDatabase
		val lastCommitId = repo.resolve("refs/heads/$BRANCH")
		val revWalk = RevWalk(repo)
		val commit = revWalk.parseCommit(lastCommitId)
		val tree = commit.tree
		val treeWalk = TreeWalk(repo)
		treeWalk.addTree(tree)
		treeWalk.isRecursive = true
		treeWalk.filter = PathFilter.create(FILE_TO_READ)
		if (!treeWalk.next()) {
			return
		}
		val objectId = treeWalk.getObjectId(0)
		val loader = repo.open(objectId)
		loader.copyTo(System.out)
	}
}