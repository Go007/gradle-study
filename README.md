# gradle-study
Gradle Study

# 不小心将忽略文件或文件夹上传到了github远程仓库，如何删除？
在github上只能删除仓库,却无法删除文件夹或文件, 所以只能通过命令来解决
首先进入你的master文件夹下, Git Bash Here ,打开命令窗口
$ git --help 帮助命令
$ git pull origin master 将远程仓库里面的项目拉下来
$ dir  查看有哪些文件夹
$ git rm -r --cached target  删除target文件夹
$ git commit -m '删除了target'  提交,添加操作说明
$ git push -u origin master 将本次更改更新到github项目上去
操作完成.
注:本地项目中的target文件夹不受操作影响,删除的只是远程仓库中的target, 可放心删除
每次增加文件或删除文件，都要commit 然后直接 git push -u origin master，就可以同步到github上了

# 解决idea的.gitignore有时不起作用的问题
有时候，.gitignore会对部分文件/文件夹失效，大概原因是由于新创建的文件已经出现在git本地仓库的缓存，所以.gitignore就失效了
解决办法就是清空一下git仓库的缓存，重新提交一次就好了
git rm -r --cached .
git add .
git commit -m 'update .gitignore'