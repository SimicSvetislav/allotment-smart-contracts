echo off

for %%f in (*) do (
	if %%~xf == .sol ( 
		solc %%f --overwrite --bin --abi --optimize -o ../backend/target/
		web3j solidity generate -b ../backend/target/%%~nf.bin -a ../backend/target/%%~nf.abi -o ../backend/src/main/java -p rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts
	)
)
