# Filestore

The **store** dynamically populates the cache with assets at runtime.  
It is designed to keep the repository lightweight and prevent unnecessary Git LFS growth.

_This step is required only once (or whenever cache content changes)._

## Setup

### Install

```bash
mvn compile -f pom.xml
```

### Run

```bash
mvn compile exec:java
```