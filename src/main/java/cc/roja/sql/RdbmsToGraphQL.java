package cc.roja.sql;

import java.util.Map;
import org.docopt.Docopt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdbmsToGraphQL {
  private static final Logger LOGGER = LoggerFactory.getLogger(RdbmsToGraphQL.class);

  private static final String VERSION="1.0-SNAPSHOT";
  private static final String DOC =
      "RDBMS to GraphQL.\n"
          + "\n"
          + "Usage:\n"
          + "  rdbms2graphql generate --jdbc-url=<JDBC_URL> --username=<USERNAME> --password=<PASSWORD>\n"
          + "\n"
          + "Options:\n"
          + "  -h --help             Show this screen.\n"
          + "  --version             Show version.\n"
          + "  --jdbc-url=<JDBC_URL> DB JDBC URL.\n"
          + "  --username=<USERNAME> DB Username.\n"
          + "  --password=<PASSWORD> DB Password.\n"
          + "  --output-dir=<OUTPUT> Output directory [default: ./generated-schema]."
          + "\n";

  public static void main(String[] args) {
    Map<String, Object> opts = new Docopt(DOC)
        .withVersion(VERSION)
        .parse(args);
    LOGGER.info(opts.toString());
  }
}
