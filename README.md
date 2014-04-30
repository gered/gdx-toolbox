# gdx-toolbox

A set of helper classes, methods, and other things built on top of libgdx which I find helpful in my own projects.
This is *not* intended to be a generic use-everywhere type of library.

**This library is still _very much_ a work in progress!**

## Using

build.gradle

    repositories {
        maven { url "http://maven.blarg.ca" }
    }

    dependencies {
        compile "ca.blarg.gdx:gdx-toolbox:0.1-SNAPSHOT"
    }

pom.xml

    <repository>
        <id>blarg.ca</id>
        <url>http://maven.blarg.ca</url>
    </repository>

    <dependency>
        <groupId>ca.blarg.gdx</groupId>
        <artifactId>gdx-toolbox</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

## License

Distributed under the the MIT License. See LICENSE for more details.
