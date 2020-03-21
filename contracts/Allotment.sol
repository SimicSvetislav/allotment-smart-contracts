pragma solidity ^0.6.0;

import './SafeMath.sol';
import './DateUtilsLibrary.sol';
import './strings.sol';

contract Allotment is SafeMath, DateUtilsLibrary, strings {
    
    uint res_id_counter = 0;
    
    struct Court {
        string name;
        string location;
    }
    
    struct Reservation {
        uint id;
        uint fromDate;
        uint toDate;
        uint number;
        uint price;
        uint priceType;
        bool provision;
        // bool verified; // Mozda se moze koristii polje provision
    }
    
    struct DateRange {
        uint startDate;
        uint endDate;
    }
    
    address payable public _agencyRepr;
    address payable public _accomodationRepr;
    
    bool public _agencyAgreed = false;
    bool public _accomodationAgreed = false;
    
    uint256 public _startDate;
    uint256 public _endDate; 
    
    uint256[] public _hotels;
    
    uint32 public _priceHB;
    uint32 public _priceFB;
    uint32 public _priceOS;
    
    uint8 public _kidAgeLimit;
    uint32 public _kidPrice;
    
    uint8 constant _smallKidLowerBound = 2;
    uint8 constant _smallKidUpperBound = 7;
    
    uint8 public _smallKidDiscount;
    
    uint8 public _offSeasonMinimum;
    uint8 public _badOffSeasonMaxPenalty;
    
    uint public _totalBeds;
    mapping (uint8 => uint) public _roomsByBedNumbers;
    
    uint16 public _informingPeriod;
    uint16 public _withdrawalPeriod;
    
    bool public _clause;
    
    uint public _advancePayment;
    uint8 public _commision;
    
    uint public _finePerBed;
    
    string public constant _law = 'Zakon o obligacionim odnosima';
    Court public _courtInCharge;
    
    // Key is number of beds
    mapping (uint => Reservation[]) _reservations;
    uint8 constant RESERVATION_PARSE_STEP = 5;
    
    // Shows if agency has restricted number of rooms during the season
    bool _restriction = false;
    
    DateRange[] _withdrawals;
    
    uint _aggreementDate;
    uint _maxBeds;
    
    enum Status { NEG, COW, REJ, DEP }
    
    Status _status;
    
    uint8[] _bedOptions;
    
    uint256 _totalReservations;
    
    modifier onlyAgencyRepr 
    {
        require(msg.sender == _agencyRepr, 'Only agency representative can execute this');
        _;
    }
    
    modifier onlyAccomodationRepr 
    {
        require(msg.sender == _accomodationRepr, 'Only accomodation representative can execute this');
        _;
    }
    
    modifier consentOfWills
    {
        require(_agencyAgreed && _accomodationAgreed, 'Agreement is not reached yet');
        _;
    }
    
    
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
    // [0] - kidAgeLimit
    // [1] - smallKidDiscount
    // [2] - offSeasonMinimum
    // [3] - badOffSeasonMaxPenalty
    //------------------------------------------
    // uint[4] memory roomsInfo
    // [0] - total number of beds
    // [1] - number of rooms with one bed
    // [2] - number of rooms with two beds
    // [3] - number of rooms with three beds
    // ... (TODO: Razmisliti o prosirenju)
    //------------------------------------------
    // uint[4] periods
    // [0] - informing period
    // [1] - withdrawal period
    //------------------------------------------
    constructor (address agencyRepr, address accomodationRepr, uint startDate, uint endDate,
                    uint[] memory hotels, uint32[4] memory prices, uint8[4] memory someConstrains, 
                    uint[] memory roomsInfo, uint16[2] memory periods, bool clause, 
                    uint advancePayment, uint8 commision, uint finePerBed, 
                    string memory courtName, string memory courtLocation) 
        public
    {
        _agencyRepr = payable(agencyRepr);
        _accomodationRepr = payable(accomodationRepr);
        
        require(isAfter(endDate, startDate), "End date must be after start date!");
        
        _startDate = startDate;
        _endDate = endDate;
        
        _hotels = hotels;
        
        assert(prices[0] <= prices[1]);
        
        _priceHB = prices[0];
        _priceFB = prices[1];
        _priceOS = prices[2];
        
        // Kid age limit must be less than 18
        assert(someConstrains[0] < 18);
        
        _kidAgeLimit = someConstrains[0];
        _kidPrice = prices[3];
        
        _smallKidDiscount = someConstrains[1];
        
        _offSeasonMinimum = someConstrains[2];
        _badOffSeasonMaxPenalty = someConstrains[3];
        
        _totalBeds = roomsInfo[0];
        _maxBeds = roomsInfo.length - 1;
        
        uint sumOfBeds = 0;
        
        for (uint8 i = 1; i < roomsInfo.length; i++) {
            _roomsByBedNumbers[i] = roomsInfo[i];
            sumOfBeds += roomsInfo[i]*i;
            if (roomsInfo[i] > 0) {
                _bedOptions.push(i);
            }
        }
        
        assert(roomsInfo[0] == sumOfBeds);
        
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
        
    }
    
    
    function delegateAgency(address newRepresentative)
        public
        onlyAgencyRepr
    {
        _agencyRepr = payable(newRepresentative);
    }
    
    
    function delegateAccomodation(address newRepresentative)
        public
        onlyAccomodationRepr
    {
        _accomodationRepr = payable(newRepresentative);
    }
    
    
    function agencyAgreed() 
        public 
        onlyAgencyRepr
        payable
    {
        // require(msg.value == _advancePayment, "Ether value not valid");
        if (_agencyAgreed == false) {
            _agencyAgreed = true;
            if (_accomodationAgreed == true) {
                // _accomodationRepr.transfer(msg.value);
                _accomodationRepr.transfer(_advancePayment);
                _aggreementDate = now;
            }
        }
        
        
    }
    
    
    function accomodationAgreed() 
        public 
        onlyAccomodationRepr
        payable
    {
        if(_accomodationAgreed == false) {
            _accomodationAgreed = true;
            if (_agencyAgreed == true) {
                _accomodationRepr.transfer(_advancePayment);
                _aggreementDate = now;
            }
        }
        
        
    }
    
    
    // uint[] memory reservation
    // [0] - from 
    // [1] - to 
    // [2] - beds
    // [3] - number of rooms
    // [4] - price type (1-half-board, 2-full-board, 3-off season)
    // repeat X times
    function reserve(uint[RESERVATION_PARSE_STEP] memory reservation) 
        public
        onlyAgencyRepr
        consentOfWills
        returns (uint totalPrice)
    {
        
        require(reservation.length % RESERVATION_PARSE_STEP == 0, 'Call parameters not properly sent');
        
        for (uint i = 0; i < reservation.length; i += RESERVATION_PARSE_STEP) {
            uint fromDate = reservation[i];
            uint toDate = reservation[i+1];
            
            // Check if dates are in valid range
            require(including(_startDate, _endDate, fromDate, toDate), 'Interval must be in range of the aggreement');
            
            uint beds = reservation[i+2];
            uint noRooms = reservation[i+3];
            uint priceType = reservation[i+4];
            
            Reservation[] memory existingRes = _reservations[beds];
            uint avaliable = _roomsByBedNumbers[uint8(beds)];
            
            require(avaliable > 0, "Rooms with desired number of beds doesn't exist");
            require(avaliable >= noRooms, "Total number of rooms is not enough");
            
            uint occupied = 0;
            
            for (uint j = 0; j < existingRes.length; j++) {
                
                Reservation memory res = existingRes[j];
                
                if (overlapping(fromDate, toDate, res.fromDate, res.toDate)) {
                    occupied += res.number;
                    require(noRooms <= avaliable - occupied, 'Not enough vacant rooms');
                }
            }
            
            // Calculate pricePerBed// 
            // TODO: Discounts
            
            uint pricePerBed;
            
            if (priceType == 1) {
                pricePerBed = _priceHB;
            } else if (priceType == 2) {
                pricePerBed = _priceFB;
            } else if (priceType == 3) {
                pricePerBed = _priceOS;
            } else {
                revert('Price type specified not valid');
            }
            
            totalPrice += beds * pricePerBed;
            
            Reservation memory newRes = Reservation(++res_id_counter, fromDate, toDate, noRooms, totalPrice, priceType, false);
            _reservations[beds].push(newRes);
            _totalReservations += 1;
            
        }
    }
    
    function withdrawRooms(uint startDate, uint endDate)
        public
        onlyAgencyRepr
        consentOfWills
        returns (uint)
    {
        require(_clause==false, "Contract with clause! Withdrawal of rooms not possible.");
        
        require(isBefore(startDate, endDate), "Date range not valid");
        
        require(isAfterOrSame(startDate, _startDate), "Start date out of range");
        
        require(isBeforeOrSame(endDate, _endDate), "End date out of range");
        
        bool onTime = isInRange(startDate, _withdrawalPeriod);
        
        if (onTime) {
            // Samo obeleziti da je slobodno
            DateRange memory drange = DateRange(startDate, endDate);
            _withdrawals.push(drange);
            return 0;
        }  else {
            DateRange memory drange = DateRange(startDate, endDate);
            _withdrawals.push(drange);
            // TODO: Sracunati odstetu
            uint itDay = startDate;
            uint notOccupiedBeds = 0;
            while (!isSame(itDay, endDate)) {
                // Odediti koliko slobodnih kreveta ima datog dana
                
                // 1. Pronaci ukupan broj kreveta
                // 2. Pronaci rezervacije za dati dan i izracunati koliko je kreveta popunjeno
                uint totalBeds = 0;
                uint occupied = 0;
                for (uint8 i = 1; i <= _maxBeds; ++i) {
                    totalBeds += _roomsByBedNumbers[i] * i;
                    
                    Reservation[] memory ress = _reservations[i];
                    
                    for (uint j = 0; j < ress.length; ++j) {
                        if (dayInRange(itDay, ress[j].fromDate, ress[j].toDate)) {
                            occupied += ress[j].number * i;
                        }
                    }
                }
                
                // 3. izracunati koliko kreveta nije popunjeno u jednom danu
                notOccupiedBeds += totalBeds - occupied;
                
                // Predji na seledeci dan
                addDays2(itDay, 1);
            }
            
            // 4. izracunati cenu koju agencija mora da plati kao odstetu za ukupan broj nepopunjenioh kreveta
            uint fine = notOccupiedBeds * _finePerBed;
            
            _accomodationRepr.transfer(fine);
            
            return fine;
        }
        
    }
    
    function breakContractAgency()
        public
        onlyAgencyRepr
        consentOfWills
    {
        // TODO: Proveriti postoje li uslovi i raspodeliti ether
        destroy();
    }
    
    function breakContractAccomodation()
        public
        onlyAgencyRepr
        consentOfWills
    {
        // TODO: Proveriti postoje li uslovi i raspodeliti ether
        destroy();
    }
    
    function verifyRoomingList(uint beds/*,uint id*/)
        public
		view
        onlyAgencyRepr
        consentOfWills
    {
        Reservation[] memory ress = _reservations[beds];
        
        for (uint i=0; i < ress.length; i++) {
            
        }
        
    }
    
    function contractBalance()
        public
        view
        returns (uint256)
    {
        return address(this).balance;        
    }
    
    function destroy() 
        private
    {
        selfdestruct(_agencyRepr);
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
    
    function transferOne()
        public
    {
        require(address(this).balance >= 1, "There is not enough ether!");
        _accomodationRepr.transfer(address(this).balance);   
    }
    
    /******************************************
    * Get data as bytes
    *******************************************/
    
    function getAllResAsBytes()
        public
        view
        returns (bytes memory b)
    {
        // uint256 step = 32*8;
        b = new bytes(32*8 * _totalReservations);
        uint offset = 0;
        for (uint8 k=0; k<_bedOptions.length; k++) {
            uint8 bedNum = _bedOptions[k];
            
            Reservation[] memory ress = _reservations[bedNum];
            
            for (uint i=0; i<ress.length; i++) {
    		    uint256 id = ress[i].id;
                uint256 fromDate = ress[i].fromDate;
                uint256 toDate = ress[i].toDate;
                uint256 number = ress[i].number;
                uint256 price = ress[i].price;
                uint256 priceType = ress[i].priceType;
                uint256 provision = ress[i].provision ? 1 : 0;
                uint256 beds_temp = bedNum;
        
    			for (uint j=0; j<32; j++) {
    			   	b[32*8*i+j+offset] = byte(uint8(id / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32+offset] = byte(uint8(fromDate / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32*2+offset] = byte(uint8(toDate / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32*3+offset] = byte(uint8(number / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32*4+offset] = byte(uint8(price / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32*5+offset] = byte(uint8(priceType / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32*6+offset] = byte(uint8(provision / (2 ** (8 * (31 - j)))));
        			b[32*8*i+j+32*7+offset] = byte(uint8(beds_temp / (2 ** (8 * (31 - j)))));
    			}
            }
            
            offset += 32*8*ress.length;
            
        }
    }
    
    function getResAsBytes(uint256 beds)
        public
        view
        returns (bytes memory b)
    {
        Reservation[] memory ress = _reservations[beds];
        
        // uint256 size = 32 * 5 * ress.length;
        uint256 step = 32*8;
        // uint256 size = 32*8 * ress.length;
        // uint256 counter = 0;
        b = new bytes(step * ress.length); 
        
        // Mock data
        /*for (uint256 k=0; k<2; k++) {
            uint256 id = 15;
    		uint256 fromDate = 1620215766;
            uint256 toDate = 1622894166;
            uint256 number = 3;
            uint256 price = 13;
            uint256 priceType = 1;
            uint256 provision = true ? 1:0;
            // counter = 0;
    		for (uint j=0; j<32; j++) {
    		    b[step*k+j] = byte(uint8(id / (2 ** (8 * (31 - j)))));
    			b[step*k+j+32] = byte(uint8(fromDate / (2 ** (8 * (31 - j)))));
    			b[step*k+j+32*2] = byte(uint8(toDate / (2 ** (8 * (31 - j)))));
    			b[step*k+j+32*3] = byte(uint8(number / (2 ** (8 * (31 - j)))));
    			b[step*k+j+32*4] = byte(uint8(price / (2 ** (8 * (31 - j)))));
    			b[step*k+j+32*5] = byte(uint8(priceType / (2 ** (8 * (31 - j)))));
    			b[step*k+j+32*6] = byte(uint8(provision / (2 ** (8 * (31 - j)))));
    			// counter++;
    		}
        }*/
        
        // require(ress.length>0, "There are no desired reservations!");
		for (uint i=0; i<ress.length; i++) {
		    uint256 id = ress[i].id;
            uint256 fromDate = ress[i].fromDate;
            uint256 toDate = ress[i].toDate;
            uint256 number = ress[i].number;
            uint256 price = ress[i].price;
            uint256 priceType = ress[i].priceType;
            uint256 provision = ress[i].provision ? 1 : 0;
            uint beds_temp = beds;
        
			for (uint j=0; j<32; j++) {
			   	b[step*i+j] = byte(uint8(id / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32] = byte(uint8(fromDate / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32*2] = byte(uint8(toDate / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32*3] = byte(uint8(number / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32*4] = byte(uint8(price / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32*5] = byte(uint8(priceType / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32*6] = byte(uint8(provision / (2 ** (8 * (31 - j)))));
    			b[step*i+j+32*7] = byte(uint8(beds_temp / (2 ** (8 * (31 - j)))));
			}
        }
        
        return b;
    }
    
    function getAA(uint256 beds)
        public
        view
        returns (uint aa)
    {
        Reservation[] memory ress = _reservations[beds];
        
        aa = ress.length;
    }
    
    function getContractInfoAsBytes()
        public
        view
        returns (bytes memory b)
    {
        /*
        uint256 startDate = _startDate;
        uint256 endDate = _endDate;
        
        // Prices
        uint256 hb = _priceHB;
        uint256 fb = _priceFB;
        uint256 os = _priceOS;
        uint256 kp = _kidPrice;
        
        uint256 kidAgeLimit = _kidAgeLimit;
        uint256 smallKidDiscount = _smallKidDiscount;
        uint256 offSeasonMinimum = _offSeasonMinimum;
        uint256 badOffSeasonMaxPenalty = _badOffSeasonMaxPenalty;
        
        uint256 wp = _withdrawalPeriod;
        uint256 ip = _informingPeriod;
        */
        
        uint256 clause = _clause ? 1 : 0;
        
        /*
        uint256 ap = _advancePayment;
        uint256 com = _commision;
        uint256 fpb = _finePerBed;
        */
        
        b = new bytes(32 * 16); 
        
        // for (uint8 i = 0; i < 16; ++i) {
        for (uint j=0; j<32; j++) {
		   	b[j+32*0] = byte(uint8(_startDate / (2 ** (8 * (31 - j)))));
			b[j+32*1] = byte(uint8(_endDate / (2 ** (8 * (31 - j)))));
			b[j+32*2] = byte(uint8(_priceHB / (2 ** (8 * (31 - j)))));
			b[j+32*3] = byte(uint8(_priceFB / (2 ** (8 * (31 - j)))));
			
			b[j+32*4] = byte(uint8(_priceOS / (2 ** (8 * (31 - j)))));
			b[j+32*5] = byte(uint8(_kidPrice / (2 ** (8 * (31 - j)))));
			b[j+32*6] = byte(uint8(_kidAgeLimit / (2 ** (8 * (31 - j)))));
			b[j+32*7] = byte(uint8(_smallKidDiscount / (2 ** (8 * (31 - j)))));
			
			b[j+32*8] = byte(uint8(_offSeasonMinimum / (2 ** (8 * (31 - j)))));
			b[j+32*9] = byte(uint8(_badOffSeasonMaxPenalty / (2 ** (8 * (31 - j)))));
			b[j+32*10] = byte(uint8(_withdrawalPeriod / (2 ** (8 * (31 - j)))));
			b[j+32*11] = byte(uint8(_informingPeriod / (2 ** (8 * (31 - j)))));
			
			b[j+32*12] = byte(uint8(clause / (2 ** (8 * (31 - j)))));
			b[j+32*13] = byte(uint8(_advancePayment / (2 ** (8 * (31 - j)))));
			b[j+32*14] = byte(uint8(_commision / (2 ** (8 * (31 - j)))));
			b[j+32*15] = byte(uint8(_finePerBed / (2 ** (8 * (31 - j)))));
			
		}
        // }
        
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
        returns (uint256[] memory hotels)
    {
        hotels = _hotels;
    }
    
    
}