FROM ubuntu:18.04

RUN apt-get update && apt-get install -y postgresql-10

ARG ERP_DATABASE_NAME
ARG ERP_DB_USERNAME
ARG ERP_DB_PASSWORD

USER postgres
RUN /etc/init.d/postgresql start && /usr/bin/psql --command "DROP DATABASE IF EXISTS $ERP_DATABASE_NAME;"

RUN    /etc/init.d/postgresql start &&\
    psql --command "CREATE USER $ERP_DB_USERNAME WITH SUPERUSER PASSWORD '$ERP_DB_PASSWORD';" &&\
    createdb -O $ERP_DB_USERNAME $ERP_DATABASE_NAME

RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/10/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/10/main/postgresql.conf

EXPOSE 5432

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

CMD ["/usr/lib/postgresql/10/bin/postgres", "-D", "/var/lib/postgresql/10/main", "-c", "config_file=/etc/postgresql/10/main/postgresql.conf"]
