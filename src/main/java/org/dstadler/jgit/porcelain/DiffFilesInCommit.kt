package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import java.io.IOException

/*
   Copyright 2013, 2014 Dominik Stadler

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
 * Simple snippet which shows how to retrieve the diffs
 * between two commits
 */
object DiffFilesInCommit {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->

				// compare older commit with the newer one, showing an addition
				// and 2 changes
				listDiff(repository, git,
						"3cc51d5cfd1dc3e890f9d6ded4698cb0d22e650e",
						"19536fe5765ee79489265927a97cb0e19bb93e70")

				// also the diffing the reverse works and now shows a delete
				// instead of the added file
				listDiff(repository, git,
						"19536fe5765ee79489265927a97cb0e19bb93e70",
						"3cc51d5cfd1dc3e890f9d6ded4698cb0d22e650e")

				// to compare against the "previous" commit, you can use
				// the caret-notation
				listDiff(repository, git,
						"19536fe5765ee79489265927a97cb0e19bb93e70^",
						"19536fe5765ee79489265927a97cb0e19bb93e70")
			}
		}
	}

	@Throws(GitAPIException::class, IOException::class)
	private fun listDiff(repository: Repository, git: Git, oldCommit: String, newCommit: String) {
		val diffs = git.diff()
				.setOldTree(prepareTreeParser(repository, oldCommit))
				.setNewTree(prepareTreeParser(repository, newCommit))
				.call()
		println("Found: " + diffs.size + " differences")
		for (diff in diffs) {
			println("Diff: " + diff.changeType + ": " +
					if (diff.oldPath == diff.newPath) diff.newPath else diff.oldPath + " -> " + diff.newPath)
		}
	}

	@Throws(IOException::class)
	private fun prepareTreeParser(repository: Repository, objectId: String): AbstractTreeIterator {
		// from the commit we can build the tree which allows us to construct the TreeParser
		RevWalk(repository).use { walk ->
			val commit = walk.parseCommit(repository.resolve(objectId))
			val tree = walk.parseTree(commit.tree.id)
			val treeParser = CanonicalTreeParser()
			repository.newObjectReader().use { reader -> treeParser.reset(reader, tree.id) }
			walk.dispose()
			return treeParser
		}
	}
}