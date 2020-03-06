package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.annotations.NonNull
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.DiffConfig
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.FollowFilter
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import java.io.IOException

// Simple example that shows how to diff a single file between two commits when
// the file may have been renamed.
object DiffRenamedFile {

    @Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repo ->
			runDiff(repo,
					"2e1d65e4cf6c5e267e109aa20fd68ae119fa5ec9",
					"5a10bd6ee431e362facb03cfe763b9a3d9dfd02d",
					"README.md")

			// try the reverse as well
			runDiff(repo,
					"5a10bd6ee431e362facb03cfe763b9a3d9dfd02d",
					"2e1d65e4cf6c5e267e109aa20fd68ae119fa5ec9",
					"README.md")

			// caret allows to specify "the previous commit"
			runDiff(repo,
					"7b2e6193a39726510ed9d0f66a779665d0e4ce23^",
					"7b2e6193a39726510ed9d0f66a779665d0e4ce23",
					"build.gradle")
		}
	}

	@Throws(IOException::class, GitAPIException::class)
	private fun runDiff(repo: Repository, oldCommit: String, newCommit: String, path: String) {
		// Diff README.md between two commits. The file is named README.md in
		// the new commit (5a10bd6e), but was named "jgit-cookbook README.md" in
		// the old commit (2e1d65e4).
		val diff = diffFile(repo,
				oldCommit,
				newCommit,
				path)

		// Display the diff
		println("Showing diff of $path")
		DiffFormatter(System.out).use { formatter ->
			formatter.setRepository(repo)
			formatter.format(diff)
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

	@NonNull
	@Throws(IOException::class, GitAPIException::class)
	private fun diffFile(repo: Repository, oldCommit: String,
	                     newCommit: String, path: String): DiffEntry? {
		val config = Config()
		config.setBoolean("diff", null, "renames", true)
		val diffConfig = config.get(DiffConfig.KEY)
		Git(repo).use { git ->
			val diffList = git.diff().setOldTree(prepareTreeParser(repo, oldCommit)).setNewTree(prepareTreeParser(repo, newCommit)).setPathFilter(FollowFilter.create(path, diffConfig)).call()
			if (diffList.size == 0) return null
			if (diffList.size > 1) throw RuntimeException("invalid diff")
			return diffList[0]
		}
	}
}