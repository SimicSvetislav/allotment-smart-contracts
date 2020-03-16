pragma solidity ^0.6.0;

import './SafeMath.sol';
import './DateUtilsLibrary.sol';

contract Allotment is SafeMath, DateUtilsLibrary {
    
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
        bool provision;
        uint price;
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
    
    uint public _startDate;
    uint public _endDate; 
    
    uint[] public _hotels;
    
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
    
    mapping (uint => Reservation[]) _reservations;
    uint8 constant RESERVATION_PARSE_STEP = 5;
    
    // Shows if agency has restricted number of rooms during the season
    bool _restriction = false;
    
    DateRange[] _withdrawals;
    
    uint _aggreementDate;
    uint _maxBeds;
    
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
        _agencyAgreed = true;
        
        if (_accomodationAgreed == true) {
            // _accomodationRepr.transfer(msg.value);
            _accomodationRepr.transfer(_advancePayment);
            _aggreementDate = now;
        }
    }
    
    
    function accomodationAgreed() 
        public 
        onlyAccomodationRepr
    {
        _accomodationAgreed = true;
        
        if (_agencyAgreed == true) {
            _accomodationRepr.transfer(_advancePayment);
            _aggreementDate = now;
        }
        
    }
    
    
    // uint[] memory reservation
    // [0] - from 
    // [1] - to 
    // [2] - beds
    // [3] - number of rooms
    // [4] - price type (1-half-board, 2-full-board, 3-off season)
    // repeat X times
    function reserve(uint[] memory reservation) 
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
            require(DateUtilsLibrary.including(_startDate, _endDate, fromDate, toDate), 'Interval must be in range of the aggreement');
            
            uint noRooms = reservation[i+2];
            uint beds = reservation[i+3];
            uint priceType = reservation[i+4];
            
            Reservation[] memory existingRes = _reservations[beds];
            uint avaliable = _roomsByBedNumbers[uint8(beds)];
            
            uint occupied = 0;
            
            for (uint j = 0; j < existingRes.length; j++) {
                
                Reservation memory res = existingRes[j];
                
                if (DateUtilsLibrary.overlapping(fromDate, toDate, res.fromDate, res.toDate)) {
                    occupied += res.number;
                    require(noRooms <= avaliable - occupied, 'Not enough vacant rooms');
                }
            }
            
            // Calculate pricpricePerBed// 
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
            
            Reservation memory newRes = Reservation(++res_id_counter, fromDate, toDate, noRooms, false, totalPrice);
            _reservations[beds].push(newRes);
            
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
        
    }
    
    function breakContractAccomodation()
        public
        onlyAgencyRepr
        consentOfWills
    {
        
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
    
}