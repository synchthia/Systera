#!/bin/bash
tsh ssh io@iomc-01 mkdir -p /home/io/iomc/plugins/update/

tsh ssh io@iomc-01 mkdir -p /home/io/iomc-arizona/local/plugins/update/
tsh ssh io@iomc-01 mkdir -p /home/io/iomc-gunma/local/plugins/update/
tsh ssh io@iomc-01 mkdir -p /home/io/iomc-osaka/local/plugins/update/
tsh ssh io@iomc-01 mkdir -p /home/io/iomc-tokachi/local/plugins/update/
tsh ssh io@iomc-01 mkdir -p /home/io/iomc-tokyo/local/plugins/update/

tsh ssh io@iomc-lobby mkdir -p /home/io/lobby/local/plugins/update/
tsh ssh io@iomc-lobby mkdir -p /home/io/lobby2/local/plugins/update/

tsh scp ./target/Systera.jar io@iomc-01:/home/io/iomc/plugins/update/

tsh scp ./target/Systera.jar io@iomc-01:/home/io/iomc-arizona/local/plugins/update/
tsh scp ./target/Systera.jar io@iomc-01:/home/io/iomc-gunma/local/plugins/update/
tsh scp ./target/Systera.jar io@iomc-01:/home/io/iomc-osaka/local/plugins/update/
tsh scp ./target/Systera.jar io@iomc-01:/home/io/iomc-tokachi/local/plugins/update/
tsh scp ./target/Systera.jar io@iomc-01:/home/io/iomc-tokyo/local/plugins/update/

tsh scp ./target/Systera.jar io@iomc-lobby:/home/io/lobby/local/plugins/update/
tsh scp ./target/Systera.jar io@iomc-lobby:/home/io/lobby2/local/plugins/update/
