# competitive-programming


A collection of solutions to various contest problems. Integrated simple SBT-based judging system.

| Contest                      | Directory              | SBT Project |
| ------------------           | ---------------------- | ----------- |
| Advent of Code 2016          | /contests/aoc/2016     | aoc-2016    |
| Advent of Code 2017          | /contests/aoc/2017     | aoc-2017    |
| Codeforces *                 | /contests/cf/*         | cf-*        |
| NCPC 2017                    | /contests/ncpc/2017    | ncpc-2017   |

The rest of the contests are waiting to be assorted.


## Using the judging system

The integrated SBT-based judging system has the following capabilities:
* Running problem code in a separate process
* Feeding a test case input file into the process' stdin
* Comparing the process' stdout to a test case output file
* Printing the process' stdout and stderr, as well as whether it differed from the test case output file and how long it took

Contest requirements:
* Located in `contests/<type>/<name>`, where `<type>` and `<name>` are arbitrary
* Contains a source folder: `src/main/scala`
* Contains a resource folder: `src/main/resources`

Problem requirements:
* Located in a contest's source folder
* Default package
* Correctly defined `main` method

Test case requirements:
* Located in a folder under a contest's resource folder with the same name as a problem
* Consists of two files `<name>.in` and `<name>.out`
* Can be placed in subfolder, so long as both files are together

### Add a new contest

```
$ mkdir -p contests/<type>/<name>
$ sbt setup
```

Replace `<type>` and `<name>` according to the contest you are creating.
The `setup` task will create the appropriate directory structure.
The SBT project corresponding to the new contest will have the name `<type>-<name>`.


### Add a new problem

```
$ sbt
> project <contest project>
> setupProblems <problem 1> <problem 2...>
```
(or if you don't care about loading times:)
```
$ sbt "<contest project>/setupProblems <problem 1> <problem 2...>"
```

This will create the file `<problem N>.scala` in `src/main/scala` and the directory `<problem N>` in `src/main/resources` for each `N` inside the contest folder.


### Add a new test case

Create two files with the same base name and with extensions `.in` and `.out`, both in the same subdirectory somewhere under `src/main/resources/<problem>` in the contest folder.


### Run all test cases

```
$ sbt
> project <contest project>
> runTests
```
(or if you don't care about loading times:)
```
$ sbt "<contest project>/runTests"
```


### Run all of one problem's test cases
```
$ sbt
> project <contest project>
> runTests <problem>
```
(or if you don't care about loading times:)
```
$ sbt "<contest project>/runTests <problem>"
```


### Run specific test cases
```
$ sbt
> project <contest project>
> runTests <problem> <test case 1> <test case 2...>
```
(or if you don't care about loading times:)
```
$ sbt "<contest project>/runTests <problem> <test case 1> <test case 2...>"
```


### Technical details

The judging system consists of 

* `project/Build.scala` - various SBT tasks
* `common/src/main/scala/common/TestHarness.scala` - the bootstrapper that runs in a new JVM instance to run one test case, examine the output, and send the result back to SBT over IPC.

The testing JVM is launched by the `runTests` task using the SBT Fork API, which has all of the necessary JVM arguments conveniently pre-prepared based on the SBT configuration.