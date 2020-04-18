pragma solidity ^0.6.0;

import './SafeMath.sol';
import './DateUtilsLibrary.sol';
import './strings.sol';

contract Allotment is SafeMath, DateUtilsLibrary, strings {
    
    struct Court {
        string name;
        string location;
    }
    
    struct Reservation {
        uint id;
        uint fromDate;
        uint toDate;
        uint numberOfRooms;
        uint price;
        uint rate;
        bool provision;
        bool mainSeason;
        uint kids;
        uint agCommision;
    }
    
    struct DateRange {
        uint startDate;
        uint endDate;
    }
    
    enum Status { NEG, COW, REJ, FIN, BRK }
    
    address payable public _agencyAddress;
    address payable public _accomodationAddress;
    
    uint public _identifiedAgRepr;
    uint public _identifiedAccRepr;
    
    bool public _agencyAgreed = false;
    bool public _accomodationAgreed = false;
    
    uint public _startDate;
    uint public _endDate; 
    
    uint[] public _hotels;
    
    uint32 public _priceHB;
    uint32 public _priceFB;
    uint32 public _priceOS;
    
    uint32 public _kidPrice;
    
    uint8 public _preSeasonMinimum;
    uint8 public _badOffSeasonMaxPenalty;
    
    uint public _totalBeds;
    mapping (uint8 => uint) public _roomsByBedNumbers;
    
    uint16 public _informingPeriod;
    uint16 public _withdrawalPeriod;
    
    bool public _clause;
    
    uint public _advancePayment;
    uint8 public _commision;
    
    uint public _finePerBed;
    
    Court public _courtInCharge;
    
    // Key is number of beds
    mapping (uint => Reservation[]) public _reservations;
    
    // Shows if agency has restricted number of rooms during the season
    bool public _restriction = false;
    
    DateRange[] public _withdrawals;
    
    uint public _agreementDate;
    
    Status public _status;
    
    uint8[] public _bedOptions;
    
    uint public _totalReservations;
    
    uint public _mainSeasonStart;
    uint public _mainSeasonEnd;
    
    // Events
    // event MyEvent(uint256 code, string msg);
    // uint256 _code = 0;
    
    modifier onlyAgencyRepr 
    {
        require(msg.sender == _agencyAddress, 'Only agency representative can execute this');
        _;
    }
    
    modifier onlyAccomodationRepr 
    {
        require(msg.sender == _accomodationAddress, 'Only accomodation representative can execute this');
        _;
    }
    
    modifier consentOfWills
    {
        require(_agencyAgreed && _accomodationAgreed, 'Agreement is not reached yet');
        _;
    }
    
    
	// -----------------------------------------
	// agencyRepr - adresa naloga agencije
	// accomodationRepr = adresa naloga ugostitelja
	// -----------------------------------------
    // Some constructor parameters sent in arrays 
    // because limit of 16 local variables was exceeded
    //------------------------------------------
    // uint32[3] memory prices
    // [0] - half-board price
    // [1] - full-board price
    // [2] - off season price
    // [3] - price for kids below age limit
    //------------------------------------------
    // uint8[4] memory someConstrains
    // [0] - kidAgeLimit -> agency representative id
    // [1] - smallKidDiscount -> accomodation representative id
    // [2] - offSeasonMinimum
    // [3] - badOffSeasonMaxPenalty
    // [4] - mainSeasonStart
    // [5] - mainSeasonEnd
    //------------------------------------------
    // uint[4] memory roomsInfo
    // [0] - total number of beds
    // [1] - number of rooms with one bed
    // [2] - number of rooms with two beds
    // [3] - number of rooms with three beds
    // [4] - ...
    // [5] - ...
    // ... 
    // NAPOMENA - obavezno je poslati nule ako postoje sobe sa odredjenim brojem kreveta koje nisu zastupljene, 
    // a pri tome postoje i sobe sa vise kreveta koje su zastupljene     
    //------------------------------------------
    // uint[4] periods
    // [0] - informing period
    // [1] - withdrawal period
    //------------------------------------------
    constructor (address agencyRepr, address accomodationRepr, uint startDate, uint endDate,
                    uint[] memory hotels, uint32[4] memory prices, uint[6] memory someConstrains, 
                    uint[] memory roomsInfo, uint16[2] memory periods, bool clause, 
                    uint advancePayment, uint8 commision, uint finePerBed, 
                    string memory courtName, string memory courtLocation) 
        public
    {
        _agencyAddress = payable(agencyRepr);
        _accomodationAddress = payable(accomodationRepr);
        
        require(isAfter(endDate, startDate), "End date must be after start date");
        
        _startDate = startDate;
        _endDate = endDate;
        
        _hotels = hotels;
        
        require(prices[0] <= prices[1], "Half-board price should be lower that full-board price");
        
        _priceHB = prices[0];
        _priceFB = prices[1];
        _priceOS = prices[2];
        
        // Kid age limit must be less than 18
        require(someConstrains[0] < 18, "Kid must be less than 18 years old");
        
        // _kidAgeLimit = uint8(someConstrains[0]);
        _kidPrice = prices[3];
        
        // _smallKidDiscount = uint8(someConstrains[1]);
        
        _preSeasonMinimum = uint8(someConstrains[2]);
        _badOffSeasonMaxPenalty = uint8(someConstrains[3]);
        
        _mainSeasonStart = someConstrains[4];
        _mainSeasonEnd = someConstrains[5];
        
        _totalBeds = roomsInfo[0];
        
        uint sumOfBeds = 0;
        
        for (uint i = 1; i < roomsInfo.length; i = add(i, 1)) {
            _roomsByBedNumbers[uint8(i)] = roomsInfo[i];
            sumOfBeds += roomsInfo[i]*i;
            if (roomsInfo[i] > 0) {
                _bedOptions.push(uint8(i));
            }
        }
        
        require(roomsInfo[0] == sumOfBeds, "Total number of beds is not correct number");
        
        _informingPeriod = periods[0];
        _withdrawalPeriod = periods[1];
        
        _clause = clause;
        
        _advancePayment = advancePayment;
        _commision = commision;
        
        _finePerBed = finePerBed;
        
        _courtInCharge.name = courtName;
        _courtInCharge.location = courtLocation;
        
        _status = Status.NEG;
        
        _totalReservations = 0;
        
        _identifiedAgRepr = someConstrains[0];
        _identifiedAccRepr = someConstrains[1];
        
        _agreementDate = 0;
        
    }
    
    function agencyAgreed(uint agReprId) 
        public 
        onlyAgencyRepr
        payable
    {
        
        if (_agencyAgreed == false) {
         
            if (_accomodationAgreed == true) {
                _accomodationAddress.transfer(_advancePayment);
                _agreementDate = now;
                _status = Status.COW;
            }
            
            _agencyAgreed = true;
            _identifiedAgRepr = agReprId;
        } else {
            revert("Agency already agreed");
        }
        
        
        // emit MyEvent(2);
        
    }
    
    
    function accomodationAgreed(uint accReprId) 
        public 
        onlyAccomodationRepr
        payable
    {
        if (_accomodationAgreed == false ) {
            
            if (_agencyAgreed == true) {
                _accomodationAddress.transfer(_advancePayment);
                _agreementDate = now;
                _status = Status.COW;
            }
            
            _accomodationAgreed = true;
            _identifiedAccRepr = accReprId;
            
        } else {
            revert("Accomodation already agreed");
        } 
        
        // emit MyEvent(1);
        
    }
    
    
    // uint[] memory reservation
    // [0] - from 
    // [1] - to 
    // [2] - beds
    // [3] - number of rooms
    // [4] - price type (1-half-board, 2-full-board)
    // [5] - number of kids
    // repeat X times
    // RESERVATION_PARSE_STEP = 6
    function reserve(uint[6] memory reservation) 
        public
        onlyAgencyRepr
        consentOfWills
        returns (uint totalPrice)
    {
        
        require(reservation.length % 6 == 0, 'Call parameters not properly sent');
        
        require( _status == Status.COW, 'Contract not active');
        
        uint commision = 0;
        for (uint i = 0; i < reservation.length; i = add(i, 6)) {
            uint fromDate = reservation[i];
            uint toDate = reservation[add(i,1)];
            
            // Check if dates are in valid range
            require(including(_startDate, _endDate, fromDate, toDate), 'Interval must be in range of the aggreement');
            
            // Check if some of  reservation dates is withdrawed 
            bool isWithdrawed = false;
            
            for (uint w=0; w<_withdrawals.length; w = add(w,1)) {
                uint wStartDate = _withdrawals[i].startDate;
                uint wEndDate = _withdrawals[i].endDate;
                
                if (overlapping(wStartDate, wEndDate, fromDate, toDate)) {
                    isWithdrawed = true;
                    break;
                }
                
            }
            
            if (isWithdrawed) {
                revert("Some dates are withdrawed");
            }
            
            uint beds = reservation[add(i,2)];
            uint noRooms = reservation[add(i,3)];
            uint fullBoard = reservation[add(i,4)];
            
            require(fullBoard == 1 || fullBoard == 2, "Full board argument not valid (must be 1 or 2)");
            
            
            
            if (!checkAvailability(beds, noRooms, fromDate, toDate)) {
                revert("Rooms not available");
            }
            
            // Calculate pricePerBed// 
            
            // Proveriti da li se rezervacija realizuje u glavnoj sezoni
            bool mainSeason = overlapping(fromDate, toDate, _mainSeasonStart, _mainSeasonEnd) ? true : false;
            
            uint pricePerBed;
            // uint priceType = 0;
            
            if (mainSeason) {
                if (fullBoard == 1) {
                    // priceType = 1;
                    pricePerBed = _priceHB;
                } else if (fullBoard == 2) {
                    // priceType = 2;
                    pricePerBed = _priceFB;
                } else {
                    revert("Price type not valid");
                }
            } else {
                // priceType = 3;
                pricePerBed = _priceOS;
            }
            
            uint kids = reservation[add(i,5)];
            
            require(kids <= mul(beds,noRooms), "Number of kids exceeds total number of beds");
            
            //totalPrice += (beds*noRooms - kids)*pricePerBed + kids*_kidPrice;
            totalPrice = add(totalPrice, add(mul(sub(mul(beds,noRooms), kids), pricePerBed), mul(kids, _kidPrice)));
            
            // commision += (beds*noRooms - kids) * pricePerBed * _commision / 100;
            commision = add(commision, div(mul(mul(sub(mul(beds,noRooms), kids), pricePerBed), _commision), 100));  
            
            _totalReservations = add(_totalReservations, 1);
            Reservation memory newRes = Reservation(_totalReservations, fromDate, toDate, noRooms, totalPrice, fullBoard, false, mainSeason, kids, commision);
            _reservations[beds].push(newRes);
        }
    }
    
    function checkAvailability(uint beds, uint noRooms, uint fromDate, uint toDate) 
        private
        view
        returns (bool)
    {
        Reservation[] memory existingRes = _reservations[beds];
        uint available = _roomsByBedNumbers[uint8(beds)];
        
        if (_restriction) {
            available = div(mul(available, _badOffSeasonMaxPenalty), 100);
        }
        
        require(available > 0, "Rooms with desired number of beds doesn't exist");
        require(available >= noRooms, "Total number of rooms is not enough");
        
        uint occupied = 0;
        
        for (uint j = 0; j < existingRes.length; j = add(j,1)) {
                
            Reservation memory res = existingRes[j];
            
            if (overlapping(fromDate, toDate, res.fromDate, res.toDate)) {
                occupied = add(occupied, res.numberOfRooms);
                require(noRooms <= sub(available, occupied), 'Not enough vacant rooms');
            }
        }
        
        return true;
    }
    
    function withdrawRooms(uint startDate, uint endDate)
        public
        onlyAgencyRepr
        consentOfWills
        returns (uint)
    {
        require(_clause==false, "Contract with clause -> withdrawal of rooms not possible.");
        
        require(isBefore(startDate, endDate), "Date range not valid");
        
        require(isAfterOrSame(startDate, _startDate), "Start date out of range");
        
        require(isBeforeOrSame(endDate, _endDate), "End date out of range");
        
        bool onTime = isInRange(startDate, _informingPeriod);
        
        // Treba li proveravati da li je vec povucen termin?
        // U principu ne moramo, nece se nista lose desiti ako se dva puta izvrsi povracaj istog termina
        
        if (onTime) {
            // Samo obeleziti da je slobodno
            DateRange memory drange = DateRange(startDate, endDate);
            _withdrawals.push(drange);
            return 0;
        }  else {
            DateRange memory drange = DateRange(startDate, endDate);
            _withdrawals.push(drange);
            // Racunanje odstete
            uint itDay = startDate;
            uint notOccupiedBeds = 0;
            while (!isSame(itDay, endDate)) {
                // Odediti koliko slobodnih kreveta ima datog dana
                
                // 1. Pronaci ukupan broj kreveta
                // 2. Pronaci rezervacije za dati dan i izracunati koliko je kreveta popunjeno
                uint totalBeds = 0;
                uint occupied = 0;
                for (uint n = 0; n <= _bedOptions.length; n=add(n,1)) {
                    
                    uint8 beds = _bedOptions[n];
                    
                    totalBeds += mul(_roomsByBedNumbers[beds], beds);
                    
                    Reservation[] memory ress = _reservations[beds];
                    
                    for (uint j = 0; j < ress.length; j=add(j,1)) {
                        if (dayInRange(itDay, ress[j].fromDate, ress[j].toDate)) {
                            occupied = add(occupied, mul(ress[j].numberOfRooms, beds));
                        }
                    }
                }
                
                // 3. izracunati koliko kreveta nije popunjeno u jednom danu
                notOccupiedBeds = add(notOccupiedBeds, sub(totalBeds, occupied));
                
                // Predji na seledeci dan
                itDay = addDays2(itDay, 1);
            }
            
            // 4. izracunati cenu koju agencija mora da plati kao odstetu za ukupan broj nepopunjenioh kreveta
            uint fine = mul(notOccupiedBeds, _finePerBed);
            
            _accomodationAddress.transfer(fine);
            
            return fine;
        }
        
    }
    
    function breakContract(uint representativeId)
        public
        consentOfWills
    {
        uint newTimestamp = addDays(_agreementDate, _withdrawalPeriod);
        
        if (isAfterOrSame(newTimestamp, now)) {
            if (representativeId == _identifiedAgRepr) {
                uint compensation = _totalBeds  * _finePerBed;
                _accomodationAddress.transfer(compensation);
            } else if (representativeId == _identifiedAccRepr) {
                uint compensation = _totalBeds  * _finePerBed + _advancePayment;
                _agencyAddress.transfer(compensation);
            } else {
                revert("Only verified representatives can break contract");
            }
        }
        
        destroy();
        
    }
    
    function verifyRoomingList(uint beds, uint reservationId)
        public
        payable
        onlyAgencyRepr
        consentOfWills
    {
        
        for (uint i=0; i < _reservations[beds].length; i=add(i,1)) {
            if (reservationId == _reservations[beds][i].id) {
                // Ne moze ovako, mora se uzeti u obzir broj krevera koje koriste deca
                // Sav prihod od takvih kreveta pripada smestaju
                // uint256 compensation = div(mul(_reservations[beds][i].price, _commision), 100);
                uint256 compensation = sub(_reservations[beds][i].price, _reservations[beds][i].agCommision);
                
                if (compensation > address(this).balance) {
                    //_code = 1;
                    revert("Can't pay compensation, balance too low");                    
                }
                
                
                _accomodationAddress.transfer(compensation);
                _reservations[beds][i].provision = true;
            }
            
        }
        
    }
    
    function checkOffseason() 
        public
        returns (uint)
    {
        if (_restriction) {
            revert("Already restricted");
        }
        
        if (isAfter(_mainSeasonStart, now)) {
            revert("Preseason not yet ended");
        }
        
        if (isAfter(now, _mainSeasonEnd)) {
            revert("Main season already ended");
        }
        
        uint256 preseasonDays = diffDays(_startDate, _mainSeasonStart);
        uint256 totalNights = mul(_totalBeds, preseasonDays);
        uint256 totalUnused = 0;
        
        uint itDay = _startDate;
        
        while (!isSame(itDay, _mainSeasonStart)) {
            
            // Prodji kroz sve rezervacije i sracunaj koliko nocenja nije rezervisano
            uint occupied = occupiedBeds(itDay);
            
            totalUnused = add(totalUnused, sub(_totalBeds, occupied));
            
            itDay = addDays(itDay, 1);
        }
        
        // Sracunati koliko procenata od ukupnog broja nocenja je iskorisceno
        // Brojevi se mnoze sa 10 000 kako bi se dobila tacnost na dve decimale 
        
        uint256 factor = 10000;
        uint256 percentUsed = div(mul(totalUnused, factor), mul(totalNights, factor));
        
        uint256 threshold = mul(_preSeasonMinimum, 100); 
        if (percentUsed < threshold) {
            _restriction = true;
        }
        
        return percentUsed;
        
    }
    
    // Doraditi
    function finalizeContract()
        public
        returns (bool)
    {
        if (isBeforeOrSame(now, _endDate)) {
            revert("Contract not yet expired");
            // return false;
        }
        
        // Finalizacija - TODO
        // 1. Provera treba li agencija da plati nadoknade
        
        uint notRealizedStays = 0;
        
        // Proci kroz sve dane 
        for (uint day = _startDate; !isSame(day, _endDate); addDays2(day, 1)) {
            
            // Proiveriti da li izvrsen povracaj kapaciteta za trenutni datum
            
            bool dateWithdrawed = false;
            for (uint i=0; i < _withdrawals.length; i=add(i,1)) {
                dateWithdrawed = dayInRange(day, _withdrawals[i].startDate, _withdrawals[i].endDate);
                if (dateWithdrawed) {
                    break;
                }
            }
            
            if (dateWithdrawed) {
                continue;
            }
            
            // Odediti koliko slobodnih kreveta ima datog dana
                
            // 1.1 Pronaci ukupan broj kreveta
            // 1.2 Pronaci rezervacije za dati dan i izracunati koliko je kreveta popunjeno
            uint totalBeds = 0;
            uint occupied = 0;
            
            
            for (uint n = 0; n <= _bedOptions.length; n=add(n,1)) {
                    
                uint8 beds = _bedOptions[n];
                
                totalBeds = add(totalBeds, mul(_roomsByBedNumbers[beds], beds));
                
                Reservation[] memory ress = _reservations[beds];
                
                for (uint j = 0; j < ress.length; j=add(j,1)) {
                    if (dayInRange(day, ress[j].fromDate, ress[j].toDate)) {
                        occupied = add(occupied, mul(ress[j].numberOfRooms, beds));
                    }
                }
            }
            
            // 1.3 izracunati koliko kreveta nije popunjeno u jednom danu
            notRealizedStays = add(notRealizedStays, sub(totalBeds, occupied));
            
        }
        
        uint totalFine = mul(notRealizedStays, _finePerBed);
        
        // 2. Slanje ukupne svote koju agencija treba da plati kao odstetu
        
        _accomodationAddress.transfer(totalFine);
        
        // 3. Slanje svote koju je ugostitelj uplatio kao depozit
        
        uint deposit = _totalBeds * _finePerBed;
        
        _accomodationAddress.transfer(deposit);
        
        // 4. Unistavanje ugovora
        destroy();
        
        return true;
        
    }
    
    // Returns number of occupied beds on certain day 
    function occupiedBeds(uint day)
        // private
        public // For testing purposes
        view
        returns (uint256)
    {
        uint256 occupied = 0;
        
        for (uint n=0; n<_bedOptions.length; n=add(n,1)) {
            uint beds = _bedOptions[n];
            
            Reservation[] memory ress = _reservations[beds];
            
            for (uint j=0; j<ress.length; j=add(j,1)) {
                if (dayInRange(day, ress[j].fromDate, ress[j].toDate)) {
                    occupied = add(occupied, mul(ress[j].numberOfRooms, beds));
                }
            }
            
        }
        
        return occupied;
    }
    
    function getInitialTransferValue()
        public
        view
        returns (uint totalValue)
    {
        // +1 -> Da bi se uzeo u obzir i poslednji dan u intervalu
        uint periodDays = diffDays(_startDate, _endDate) + 1;
        uint mainSeasonDays = diffDays(_mainSeasonStart, _mainSeasonEnd) + 1;
        
        uint offSeasonDays = sub(periodDays, mainSeasonDays);
        
        // Ukupna inicijalna uplata
        totalValue = _advancePayment + offSeasonDays * _totalBeds * _priceOS + mainSeasonDays * _totalBeds * _priceFB;
        
        uint256 offSeasonValue = mul(mul(_totalBeds, _priceOS), offSeasonDays);
        uint256 mainSeasonValue = mul(mul(_totalBeds, _priceFB), mainSeasonDays);
        
        totalValue = add(add(offSeasonValue, mainSeasonValue), _advancePayment);
        
        return totalValue;
    }
    
    function contractBalance()
        public
        view
        returns (uint)
    {
        return address(this).balance;        
    }
    
    function destroy() 
        private
    {
        selfdestruct(_agencyAddress);
    }
    
    fallback ()
        external
        payable 
    { 
          
    }
    
    receive ()
        external
        payable 
    { 
          
    }
    
    /******************************************
    * Get data as bytes
    * SafeMath not used becasuse those are view functions
    *******************************************/
    
    function getAllResAsBytes()
        public
        view
        returns (bytes memory b)
    {
        uint256 step = 32*10;
        b = new bytes(step * _totalReservations);
        uint offset = 0;
        for (uint8 k=0; k<_bedOptions.length; k++) {
            uint8 bed_num = _bedOptions[k];
            
            Reservation[] memory ress = _reservations[bed_num];
            
            for (uint i=0; i<ress.length; i++) {
                uint256 provision = ress[i].provision ? 1 : 0;
                uint256 mainSeason = ress[i].mainSeason ? 1 : 0;
        
    			for (uint j=0; j<32; j++) {
    			   	b[step*i+j+offset] = byte(uint8(ress[i].id / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32+offset] = byte(uint8(ress[i].fromDate / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*2+offset] = byte(uint8(ress[i].toDate / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*3+offset] = byte(uint8(ress[i].numberOfRooms / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*4+offset] = byte(uint8(ress[i].price / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*5+offset] = byte(uint8(ress[i].rate / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*6+offset] = byte(uint8(provision / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*7+offset] = byte(uint8(bed_num / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*8+offset] = byte(uint8(mainSeason / (2 ** (8 * (31 - j)))));
        			b[step*i+j+32*9+offset] = byte(uint8(ress[i].kids / (2 ** (8 * (31 - j)))));
    			}
            }
            offset += step*ress.length;
        }
    }
    
    function getWithdrawalsAsBytes() 
        public
        view
        returns (bytes memory b)
    {
        uint256 len = _withdrawals.length;
        
        uint256 step = 32*2;
        // uint256 counter = 0;
        b = new bytes(step * len); 
        
        for (uint i=0; i<len; ++i) {
            uint256 startDate = _withdrawals[i].startDate;
            uint256 endDate = _withdrawals[i].endDate;
            
            for (uint j=0; j<32; j++) {
                b[step*i+j] = byte(uint8(startDate / (2 ** (8 * (31 - j)))));
        		b[step*i+j+32] = byte(uint8(endDate / (2 ** (8 * (31 - j)))));
            }
        }
    }
    
    function getContractInfoAsBytes()
        public
        view
        returns (bytes memory b)
    {
        
        uint256 clause = _clause ? 1 : 0;
        
        b = new bytes(32 * 22); 
        
        for (uint j=0; j<32; j++) {
		   	b[j+32*0] = byte(uint8(_startDate / (2 ** (8 * (31 - j)))));
			b[j+32*1] = byte(uint8(_endDate / (2 ** (8 * (31 - j)))));
			b[j+32*2] = byte(uint8(_priceHB / (2 ** (8 * (31 - j)))));
			b[j+32*3] = byte(uint8(_priceFB / (2 ** (8 * (31 - j)))));
			
			b[j+32*4] = byte(uint8(_priceOS / (2 ** (8 * (31 - j)))));
			b[j+32*5] = byte(uint8(_kidPrice / (2 ** (8 * (31 - j)))));
			b[j+32*6] = byte(uint8(0 / (2 ** (8 * (31 - j)))));
			b[j+32*7] = byte(uint8(0 / (2 ** (8 * (31 - j)))));
			
			b[j+32*8] = byte(uint8(_preSeasonMinimum / (2 ** (8 * (31 - j)))));
			b[j+32*9] = byte(uint8(_badOffSeasonMaxPenalty / (2 ** (8 * (31 - j)))));
			b[j+32*10] = byte(uint8(_informingPeriod / (2 ** (8 * (31 - j)))));
			b[j+32*11] = byte(uint8(_withdrawalPeriod / (2 ** (8 * (31 - j)))));
			
			b[j+32*12] = byte(uint8(clause / (2 ** (8 * (31 - j)))));
			b[j+32*13] = byte(uint8(_advancePayment / (2 ** (8 * (31 - j)))));
			b[j+32*14] = byte(uint8(_commision / (2 ** (8 * (31 - j)))));
			b[j+32*15] = byte(uint8(_finePerBed / (2 ** (8 * (31 - j)))));
			
			b[j+32*16] = byte(uint8(address(this).balance / (2 ** (8 * (31 - j)))));
			b[j+32*17] = byte(uint8(_agreementDate / (2 ** (8 * (31 - j)))));
			b[j+32*18] = byte(uint8(_identifiedAgRepr / (2 ** (8 * (31 - j)))));
			b[j+32*19] = byte(uint8(_identifiedAccRepr / (2 ** (8 * (31 - j)))));
			
			b[j+32*20] = byte(uint8(_mainSeasonStart / (2 ** (8 * (31 - j)))));
			b[j+32*21] = byte(uint8(_mainSeasonEnd / (2 ** (8 * (31 - j)))));
			
		}

        return b;
        
    }
    
    function getCourtInfo()
        public
        view
        returns (string memory court)
    {
        string memory at = "@";
        string memory temp = concat(toSlice(_courtInCharge.name), toSlice(at));
        court = concat(toSlice(temp), toSlice(_courtInCharge.location));
    }
    
    function getRoomsInfo()
        public
        view
        returns (bytes memory b)
    {
        uint step = 32*2;
        
        b = new bytes(step * _bedOptions.length); 
        
        for (uint i=0; i<_bedOptions.length; ++i) {
            uint8 beds = _bedOptions[i];
            uint256 rooms = _roomsByBedNumbers[beds];
            
            for (uint j=0; j<32; j++) {
                b[step*i+j] = byte(uint8(beds / (2 ** (8 * (31 - j)))));
			    b[step*i+j+32] = byte(uint8(rooms / (2 ** (8 * (31 - j)))));
            }
        }
    }
    
    function getHotels()
        public
        view
        returns (uint[] memory hotels)
    {
        hotels = _hotels;
    }
    
}