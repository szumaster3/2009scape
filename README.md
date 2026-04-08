# 2009Scape Fork

A fork of [2009Scape](https://gitlab.com/2009scape/2009scape), licensed under **AGPL-3.0**.

## Prerequisites

Before setting up the project, make sure you have the following installed:

- **Java 11**
    - [Adoptium Temurin 11](https://adoptium.net/temurin/releases/?version=11)
    - [Oracle Java 11](https://www.oracle.com/java/technologies/downloads/#java11)

- **IntelliJ IDEA**
    - [Download IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

> **Windows users:** Enable **Developer Mode** before continuing. This ensures symbolic links (symlinks) work correctly in the repository.


## Fork & Clone

1. Fork the repository on GitLab.
2. Clone your fork:

```
git clone <your-fork-ssh-or-https-url>
```

**Windows users:** Git has a file path length limit. If you see an error during `git clone`, run:

```
git config --global core.longpaths true
```

3. Enter the project directory:

```
cd <your-project-folder>
```

## Import Project in IntelliJ

1. Open IntelliJ IDEA.
2. Select **File → Open...** and choose the project root directory.
3. IntelliJ will detect `pom.xml` and import the Maven project.
4. Set the **Project SDK** to **Java 11** or newer.


## Setup Git & SSH

Generate an SSH key if needed:

```
ssh-keygen -t ed25519 -C "example@example.eu"
```

Configure Git with your user info:

```
git config --global user.name "Your Name"
git config --global user.email "example@example.eu"
```

## Build Project

```
mvn clean install
```

## Run Project

```
mvn exec:java -f pom.xml
```

## Contributing

```
git checkout -b feature/my-feature
git commit -am "Describe your changes"
git push
```

## Troubleshooting

- Check Java version:

```
java -version
```

- Check Maven version:

```
mvn -version
```

## License

All modules in this repository are licensed under the **AGPL-3.0**, unless otherwise specified.  
See [LICENSE](LICENSE) or [https://www.gnu.org/licenses/agpl-3.0.html](https://www.gnu.org/licenses/agpl-3.0.html) for details.
