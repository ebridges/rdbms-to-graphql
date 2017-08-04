[![CircleCI Status](https://circleci.com/gh/ebridges/rdbms-to-graphql.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/ebridges/rdbms-to-graphql) [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome)


## RDBMS to GraphQL

This is a command line tool useful for generating a GraphQL schema from an RDBMS database. It currently works with MySQL
& PostgreSQL.  It can be extended to support other databases.

It offers support for reading tables & columns in a database, detecting foreign key relationships and representing these
accurately as a GraphQL schema useful in a GraphQL server for querying the database.

### Usage

```
RDBMS to GraphQL.

Usage:
  rdbms2graphql generate --jdbc-url=<JDBC_URL> --jdbc-driver=<JDBC_DRIVER> --username=<USERNAME> --password=<PASSWORD> [--schema=<SCHEMA>] [--tables=<TABLES>] [--output-dir=<OUTPUT>] [--verbose]
  rdbms2graphql --version
  rdbms2graphql [-h|--help]

Options:
  -h --help                   Show this screen.
  --version                   Show version.
  --verbose                   Verbose logging.
  --jdbc-url=<JDBC_URL>       DB JDBC URL.
  --jdbc-driver=<JDBC_DRIVER> FQCN of driver class.
  --username=<USERNAME>       DB Username.
  --password=<PASSWORD>       DB Password.
  --output-dir=<OUTPUT>       Output directory [default: ./generated-schema].
  --schema=<SCHEMA>           Output directory [default: '%'].
  --tables=<TABLES>           CSV list of tables to include [default: '%'].
```
