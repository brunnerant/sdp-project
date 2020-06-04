
# QEDit - An android app to create and answer math quizzes
Developed by Anthony Iozzia, Alexis Cohen, Cosme Jordan, Nicolas Bähler, Nathan Greslin and Antoine Brunner in
the context of a project at EPFL.

[![Build Status](https://travis-ci.org/brunnerant/sdp-project.svg?branch=master)](https://travis-ci.org/brunnerant/sdp-project)
[![Test Coverage](https://api.codeclimate.com/v1/badges/97269ca5f086f0c8ed40/test_coverage)](https://codeclimate.com/github/brunnerant/sdp-project/test_coverage)
[![Maintainability](https://api.codeclimate.com/v1/badges/97269ca5f086f0c8ed40/maintainability)](https://codeclimate.com/github/brunnerant/sdp-project/maintainability)

## Wiki
We have a Wiki where we keep track of some of our design choices, and were we collect some useful information about android
programming. Check it out [here](https://github.com/brunnerant/sdp-project/wiki).

## Pull request policy
In order to merge your branch into master, you will need all the status checks to pass and two reviews from your peers.
The Travis CI system will run all the tests on your commit, so you cannot merge until they all pass.
CodeClimate is also ensuring that the test coverage stays above 80% and that the code has some quality standards.

In addition to all of this, you will not be able to merge if your code style does not conform to what is expected.
To check locally that the code style is acceptable, you can run:
```
./gradlew spotlessCheck
```
You can also auto-format your code by running:
```
./gradlew spotlessApply
```
Voila, have fun developing this android app!!!