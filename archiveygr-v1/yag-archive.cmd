SET YAG_LIB=lib
SET YAG_CP=%YAG_LIB%\activation.jar;%YAG_LIB%\commons-codec-1.3.jar;%YAG_LIB%\commons-collections-3.2.jar
SET YAG_CP=%YAG_CP%;%YAG_LIB%\commons-httpclient-3.0.1.jar;%YAG_LIB%\commons-io-1.2.jar
SET YAG_CP=%YAG_CP%;%YAG_LIB%\commons-lang-2.1.jar;%YAG_LIB%\commons-logging-1.1.jar
SET YAG_CP=%YAG_CP%;%YAG_LIB%\htmlunit-1.10.jar;%YAG_LIB%\jakarta-oro-2.0.8.jar
SET YAG_CP=%YAG_CP%;%YAG_LIB%\jaxen-1.1-beta-11.jar;%YAG_LIB%\js.jar;%YAG_LIB%\log4j.jar;%YAG_LIB%\mail.jar
SET YAG_CP=%YAG_CP%;%YAG_LIB%\mysql-connector-java-5.0.5-bin.jar;%YAG_LIB%\nekohtml-0.9.5.jar
SET YAG_CP=%YAG_CP%;%YAG_LIB%\xercesImpl-2.6.2.jar;%YAG_LIB%\xmlParserAPIs-2.6.2.jar

SET YAG_CLASS=net.namonamaha.archive.ygroups.ArchiveYahooGroup

// No Proxy, Direct Connection to Internet
SET YAG_PROXY=

// Through Proxy Server
REM SET YAG_PROXY=-DproxyServer=squid.is.chrysler.com -DproxyPort=8085

// Through Proxy Server and requires user id and password
rem SET YAG_PROXY=-DproxyServer=squid.is.chrysler.com -DproxyPort=8085 -DproxyUser=t3489mm -DproxyPasswd=4everyone

REM %1 - // Yahoo Login Id
REM %2 - // Yahoo Login Password
REM %3 - // Download from message no
REM %4 - // Download x no of messages
REM %5 - // Batch Count
REM %6 - // Sleep time for each Batch Count in milliseconds 1000 => 1 second

SET YAG_JDBC_URL=-Dyag_jdbc_url=jdbc:mysql://mysql.namo-namaha.net:3306/bsabha
SET YAG_GROUP_NAME=-Dyag_group_name=Brahmasabha
SET YAG_JDBC_USER_ID=-Djdbc_user_id=bsabha
SET YAG_JDBC_PWD=-Djdbc_pwd=bsabha1234

REM SET YAG_JDBC_URL=-Dyag_jdbc_url=jdbc:mysql://mysql.namo-namaha.net:3306/ygrp_ps
REM SET YAG_GROUP_NAME=-Dyag_group_name=B
REM SET YAG_JDBC_USER_ID=-Djdbc_user_id=ygrp_ps
REM SET YAG_JDBC_PWD=-Djdbc_pwd=ygrp1234

REM java -cp bin;etc;%YAG_CP% %YAG_PROXY% %YAG_CLASS% %1 %2 %3 %4 %5 %6
REM java %YAG_JDBC_URL% %YAG_GROUP_NAME% %YAG_JDBC_USER_ID% %YAG_JDBC_PWD% -cp bin;etc;%YAG_CP% %YAG_PROXY% %YAG_CLASS% ps.archive ps12345 0 -1 1 8000
java %YAG_JDBC_URL% %YAG_GROUP_NAME% %YAG_JDBC_USER_ID% %YAG_JDBC_PWD% -cp bin;etc;%YAG_CP% %YAG_PROXY% %YAG_CLASS% thirumalaikv sonofakila 0 -1 1 8000

pause
