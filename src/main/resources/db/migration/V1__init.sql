create table voters (id bigserial primary key,
                     first_name varchar(100) not null,
                     last_name  varchar(100) not null,
                     pesel      varchar(11)  not null unique,
                     blocked    boolean      not null default false
);

create table elections (id bigserial primary key,
                        name varchar(255) not null,
                        start_at timestamptz,
                        end_at   timestamptz,
                        status varchar(30) not null
);

create table election_options (id bigserial primary key,
                               election_id bigint not null references elections(id) on delete cascade,
                               name varchar(255) not null,
                               constraint uq_option_name_per_election unique (election_id, name)
);


create table votes ( id bigserial primary key,
                     voter_id bigint not null references voters(id),
                     election_id bigint not null references elections(id),
                     option_id bigint not null references election_options(id),
                     cast_at timestamptz not null,
                     constraint uq_vote_per_voter_per_election unique (voter_id, election_id)
);
