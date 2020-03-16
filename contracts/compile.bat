echo off

set filename=Allotment

solc %filename%.sol --overwrite --bin --abi --optimize -o ../backend/target/
web3j solidity generate -b ../backend/target/%filename%.bin -a ../backend/target/%filename%.abi -o ../backend/src/main/java -p rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts
