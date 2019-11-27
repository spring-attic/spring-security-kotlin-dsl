_Have something you'd like to contribute to the framework? We welcome pull requests, but ask that you carefully read this document first to understand how best to submit them; what kind of changes are likely to be accepted; and what to expect from the Spring Security team when evaluating your submission._

_Please refer back to this document as a checklist before issuing any pull request; this will save time for everyone!_

# Code of Conduct
This project adheres to the Contributor Covenant [code of conduct](CODE_OF_CONDUCT.adoc).
By participating, you  are expected to uphold this code. Please report unacceptable behavior to spring-code-of-conduct@pivotal.io.

# Understand the basics 
Not sure what a pull request is, or how to submit one? Take a look at GitHub's excellent [help documentation first](https://help.github.com/articles/using-pull-requests).

# Sign the Contributor License Agreement

If you have not previously done so, please fill out and
submit the [Contributor License Agreement](https://cla.pivotal.io/sign/spring).

# Create your branch from master
Create your topic branch to be submitted as a pull request from master. The Spring team will consider your pull request for backporting on a case-by-case basis; you don't need to worry about submitting anything for backporting.

# Use short branch names
Branches used when submitting pull requests should preferably be named according to GitHub issues, e.g. 'gh-1234' or 'gh-1234-fix-npe'. Otherwise, use succinct, lower-case, dash (-) delimited names, such as 'fix-warnings', 'fix-typo', etc. This is important, because branch names show up in the merge commits that result from accepting pull requests, and should be as expressive and concise as possible.

# Keep commits focused

Remember each ticket should be focused on a single item of interest since the tickets are used to produce the changelog. Since each commit should be tied to a single GitHub issue, ensure that your commits are focused. For example, do not include an update to a transitive library in your commit unless the GitHub is to update the library. Reviewing your commits is essential before sending a pull request.

# Mind the whitespace
Please carefully follow the whitespace and formatting conventions already present in the framework. 

1. Tabs, not spaces
1. Unix (LF), not dos (CRLF) line endings
1. Eliminate all trailing whitespace
1. Aim to wrap code at 120 characters, but favor readability over wrapping
1. Preserve existing formatting; i.e. do not reformat code for its own sake
1. Search the codebase using git grep and other tools to discover common naming conventions, etc.
1. UTF-8 encoding for Java sources

# Add Apache license header to all new classes

<pre>
/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ...;
</pre>
# Update Apache license header to modified files as necessary
Always check the date range in the license header. For example, if you've modified a file in 2012 whose header still reads
<pre>
 * Copyright 2002-2011 the original author or authors.
</pre>
then be sure to update it to 2012 appropriately
<pre>
 * Copyright 2002-2012 the original author or authors.
</pre>

# Submit JUnit test cases for all behavior changes
Search the codebase to find related unit tests and add additional `@Test` methods within. 

1. Any new tests should end in the name Tests (note this is plural). For example, a valid name would be `FilterChainProxyTests`. An invalid name would be `FilterChainProxyTest`.
2. New test methods should not start with test. This is an old JUnit3 convention and is not necessary since the method is annotated with @Test.

# Squash commits
Use `git rebase --interactive`, `git add --patch` and other tools to "squash" multiple commits into atomic changes. In addition to the man pages for git, there are many resources online to help you understand how these tools work. Here from the [Git SCM Book](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History).

# Use real name in git commits
Please configure git to use your real first and last name for any commits you intend to submit as pull requests. For example, this is not acceptable:

<pre>
Author: Nickname &lt;user@mail.com&gt;
</pre>
Rather, please include your first and last name, properly capitalized, as submitted against the SpringSource contributor license agreement:
<pre>
Author: First Last &lt;user@mail.com&gt;
</pre>
This helps ensure traceability against the CLA, and also goes a long way to ensuring useful output from tools like git shortlog and others.

You can configure this globally via the account admin area GitHub (useful for fork-and-edit cases); globally with

<pre>
git config --global user.name "First Last"
git config --global user.email user@mail.com
</pre>

or locally only by omitting the '--global' flag:
<pre>
git config user.name "First Last"
git config user.email user@mail.com
</pre>

# Format commit messages

<pre>
Short (50 chars or less) summary of changes

More detailed explanatory text, if necessary.  Wrap it to about 72
characters or so.  In some contexts, the first line is treated as the
subject of an email and the rest of the text as the body.  The blank
line separating the summary from the body is critical (unless you omit
the body entirely); tools like rebase can get confused if you run the
two together.

Further paragraphs come after blank lines.

 - Bullet points are okay, too

 - Typically a hyphen or asterisk is used for the bullet, preceded by a
   single space, with blank lines in between, but conventions vary here

Fixes gh-123
</pre>


1. Keep the subject line to 50 characters or less if possible
2. Do not end the subject line with a period
3. In the body of the commit message, explain how things worked before this commit, what has changed, and how things work now
3. Include Fixes gh-<issue-number> at the end if this fixes a GitHub issue  
5. Avoid markdown, including back-ticks identifying code

# Run all tests prior to submission

<pre>
./gradlew clean build test
</pre>

# Submit your pull request
Subject line:

Follow the same conventions for pull request subject lines as mentioned above for commit message subject lines.

In the body:

1. Mention the associated GitHub Issue
1. Add any additional information and ask questions; start a conversation, or continue one from GitHub Issues
1. Also mention that you have submitted the CLA as described above
Note that for pull requests containing a single commit, GitHub will default the subject line and body of the pull request to match the subject line and body of the commit message. This is fine, but please also include the items above in the body of the request.

# Mention your pull request on the associated GitHub issue
Add a comment to the associated GitHub issue(s) linking to your new pull request.

# Expect discussion and rework
The Spring team takes a very conservative approach to accepting contributions to the framework. This is to keep code quality and stability as high as possible, and to keep complexity at a minimum. Your changes, if accepted, may be heavily modified prior to merging. You will retain "Author:" attribution for your Git commits granted that the bulk of your changes remain intact. You may be asked to rework the submission for style (as explained above) and/or substance. Again, we strongly recommend discussing any serious submissions with the Spring Framework team prior to engaging in serious development work.

Note that you can always force push (git push -f) reworked / rebased commits against the branch used to submit your pull request. i.e. you do not need to issue a new pull request when asked to make changes.
