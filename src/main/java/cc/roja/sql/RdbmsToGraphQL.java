package cc.roja.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.docopt.Docopt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.roja.sql.model.Entity;

public class RdbmsToGraphQL {
  private static final Logger LOGGER = LoggerFactory.getLogger(RdbmsToGraphQL.class);

  private static final String VERSION="1.0-SNAPSHOT";
  private static final String DOC =
      "RDBMS to GraphQL.\n"
          + "\n"
          + "Usage:\n"
          + "  rdbms2graphql generate --jdbc-url=<JDBC_URL> --jdbc-driver=<JDBC_DRIVER> --username=<USERNAME> --password=<PASSWORD> [--schema=<SCHEMA>] [--tables=<TABLES>] [--output-dir=<OUTPUT>] [--verbose]\n"
          + "  rdbms2graphql --version\n"
          + "  rdbms2graphql [-h|--help]\n"
          + "\n"
          + "Options:\n"
          + "  -h --help                   Show this screen.\n"
          + "  --version                   Show version.\n"
          + "  --verbose                   Verbose logging.\n"
          + "  --jdbc-url=<JDBC_URL>       DB JDBC URL.\n"
          + "  --jdbc-driver=<JDBC_DRIVER> FQCN of driver class.\n"
          + "  --username=<USERNAME>       DB Username.\n"
          + "  --password=<PASSWORD>       DB Password.\n"
          + "  --output-dir=<OUTPUT>       Output directory [default: ./generated-schema].\n"
          + "  --schema=<SCHEMA>           Output directory [default: '%'].\n"
          + "  --tables=<TABLES>           CSV list of tables to include [default: '%'].\n"
          + "\n";

  public static void main(String[] args) throws Exception {
    Map<String, Object> opts = new Docopt(DOC)
        .withVersion(VERSION)
        .parse(args);

    if(hasOpt(opts, "verbose")) {
      configureVerboseLogging();
    }

    LOGGER.debug(opts.toString());

    String[] includedTables = initializeIncludedTables(opts);
    List<Entity> entities;
    try(DatabaseAnalyzer analyzer = initializeDatabaseAnalyzer(opts)) {
      entities = analyzer.initializeEntities(includedTables);
      LOGGER.debug("entities: "+ entities);
    }

    SchemaWriter writer = new SchemaWriter(getOpt(opts, "output-dir"));
    writer.writeEntities(entities);
  }

  private static String[] initializeIncludedTables(Map<String, Object> opts) {
    String tableCsv = getOpt(opts, "tables");
    return tableCsv.split("\\s*,\\s*");
  }

  private static DatabaseAnalyzer initializeDatabaseAnalyzer(Map<String, Object> opts) {
    String jdbcUrl = getOpt(opts, "jdbc-url");
    String driver = getOpt(opts, "jdbc-driver");
    String username = getOpt(opts, "username");
    String password = getOpt(opts, "password");
    String schema = getOpt(opts, "schema");

    DatabaseAnalyzer analyzer = null;
    try {
      analyzer = new DatabaseAnalyzer(jdbcUrl, driver, username, password, schema);
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
      LOGGER.error("unable to configure database metadata.", e);
    }
    return analyzer;
  }

  @SuppressWarnings("SameParameterValue")
  private static boolean hasOpt(Map<String, Object> opts, String arg) {
    boolean present = false;
    if(opts.containsKey("--"+arg)) {
      Object val = opts.get("--"+arg);
      if(val != null) {
        return Boolean.getBoolean(val.toString());
      }
    }
    //noinspection ConstantConditions
    return present;
  }

  private static String getOpt(Map<String, Object> opts, String arg) {
    Object o = opts.get("--"+arg);
    if(o != null) {
      return o.toString().replaceAll("'", "");
    }
    throw new IllegalArgumentException("No param found for arg: "+arg);
  }

  private static void configureVerboseLogging() {
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(ch.qos.logback.classic.Level.DEBUG);
  }
}
