import { WithdrawalDTO } from './../WithdrawalDTO';
import { Period } from './../Period';
import { Reservation, SingleReservation, ReservationDTO } from './../Reservation';
import { RoomsInfo, RoomsInfoKids } from './../RoomsInfo';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { Web3jService } from './../services/web3j.service';
import { Contract } from './../Contract';
import { AuthService } from './../services/auth/auth.service';
import { Component, OnInit } from '@angular/core';
import { Representative } from '../Representative';

@Component({
  selector: 'app-proposals',
  templateUrl: './proposals.component.html',
  styleUrls: ['./proposals.component.scss']
})
export class ProposalsComponent implements OnInit {

  constructor(private authService: AuthService, private web3j: Web3jService,
              private router: Router, private toastr: ToastrService) { }

  repr: Representative;

  contracts: Array<Contract>;
  contractsCOW: Array<Contract>;

  roomsInfoStrings = new Map<number,string>()

  startDate: NgbDateStruct;
  endDate: NgbDateStruct;
  period = new Period();

  rik = new RoomsInfoKids();
  roomsError = false;

  reserveStartDate: NgbDateStruct;
  reserveEndDate: NgbDateStruct;

  date1Error1 = false;
  date1Error2 = false;
  date2Error1 = false;
  date2Error2 = false;

  date3Error1 = false;
  date3Error2 = false;
  date4Error1 = false;
  date4Error2 = false;

  cbError = false;

  reservation = new Reservation();

  contractID: number;

  reservationsDTO = new Array<ReservationDTO>();
  withdrawalsDTO = new Array<WithdrawalDTO>();

  fundsValue: number;

  isCollapsed = new Map<number, boolean>();

  ngOnInit() {

    this.authService.checkLoggedIn();
    this.authService.isUserLoggedIn.next(true);

    this.authService.setActiveTab('Proposals');

    this.repr = this.authService.getUser();

    this.populate();
  }

  populate() {

    this.contracts = [];
    this.contractsCOW = [];

    this.web3j.getContractsByOrgAndStatus(this.repr.orgId, 'NEG').subscribe((data: Array<Contract>) =>{
      /*data.forEach(element => {
        alert(element.courtName);
      });*/
      this.contracts = data;
      this.contracts.forEach(c => {
        let cStr = '';
        for (let i=1; i<c.roomsInfo.length; ++i) {
          if (c.roomsInfo[i] !== 0) {
            if (i!==1) {
              cStr += i + ' beds - '
            } else {
              cStr += i + ' bed - '
            }

            if (c.roomsInfo[i] === 1) {
              cStr += '1 room'
            } else {
              cStr += c.roomsInfo[i] + ' rooms';
            }
            cStr += ';  ';
          }
          this.isCollapsed.set(c.id, false);
        }
        this.roomsInfoStrings.set(c.id, cStr);
      });
      // alert(this.contracts.length)
    }, error => console.log(error));

    this.web3j.getContractsByOrgAndStatus(this.repr.orgId, 'COW').subscribe((data: Array<Contract>) =>{
      /*data.forEach(element => {
        alert(element.courtName);
      });*/
      this.contractsCOW = data;
      this.contractsCOW.forEach(c => {
        let cStr = '';
        for (let i=1; i<c.roomsInfo.length; ++i) {
          if (c.roomsInfo[i] !== 0) {
            if (i!==1) {
              cStr += i + ' beds - '
            } else {
              cStr += i + ' bed - '
            }

            if (c.roomsInfo[i] === 1) {
              cStr += '1 room'
            } else {
              cStr += c.roomsInfo[i] + ' rooms';
            }
            cStr += ';  ';
          }
          this.isCollapsed.set(c.id, false);
        }
        this.roomsInfoStrings.set(c.id, cStr);
      });
    }, error => console.log(error));

  }

  accept(id: number) {
    this.web3j.accept(id, this.repr.id).subscribe(data => {
      this.populate();
    }, error => console.error(error));
  }

  reject(id: number) {
    this.web3j.reject(id).subscribe(data => {
      this.toastr.warning('Proposal rejected')
      this.populate();
    }, error => console.error(error));
  }

  counteroffer(id: number) {
    this.authService.setActiveTab('Propose');

    this.router.navigateByUrl('/propose/' + id);

    return false;
  }

  addRik(f2: any) {

    this.roomsError = false;

    if (this.rik.beds * this.rik.noRooms < this.rik.kids) {
      this.toastr.error('Number of kids exceeds number of beds');
      return;
    }

    let exists = false;
    this.reservation.roomsInfo.forEach(item => {
      if (item.beds === this.rik.beds) {
        item.noRooms += this.rik.noRooms;
        item.kids += this.rik.kids;
        exists = true;
        this.rik = new RoomsInfoKids();
        return;
      }
    });

    if (!exists) {
      // let riTemp = new RoomsInfo();
      // riTemp.noRooms = this.ri.noRooms;
      // riTemp.beds = this.ri.beds;
      this.reservation.roomsInfo.push(this.rik);
      this.rik = new RoomsInfoKids();
    }

    this.reservation.roomsInfo = this.reservation.roomsInfo.sort((a,b) => {
      if (a.beds > b.beds) {
        return 1;
      }

      if (a.beds < b.beds) {
        return -1;
      }

      return 0;

    });

    f2.submitted = false;
  }

  removeRoomsInfo(rInfo: RoomsInfo) {
    for (let i=0; i<this.reservation.roomsInfo.length; ++i) {
      if (rInfo.beds === this.reservation.roomsInfo[i].beds) {
        this.reservation.roomsInfo.splice(i, 1);
        if (this.reservation.roomsInfo.length === 0) {
          this.roomsError = true;
        }
        break;
      }
    }
  }

  validate(form1, form2) {

    form2.submitted = false;

    let error = false;

    if (!this.reserveStartDate) {
      this.date1Error1 = true;
      error = true;
    } else if (!(this.reserveStartDate instanceof Object)) {
      this.date1Error2 = true;
      error = true;
    }

    if (!this.reserveEndDate) {
      this.date2Error1 = true;
      error = true;
    } else if (!(this.reserveEndDate instanceof Object)) {
      this.date2Error2 = true;
      error = true;
    }

    if (this.reservation.roomsInfo.length === 0) {
      // this.toastr.error('Please select at least one room');
      error = true;
      this.roomsError = true;
    }

    if (this.reservation.priceType===0) {
      this.cbError = true;
      error = true;
    }

    if (error) {
      return false;
    }

    return form1.form.valid;

  }

  validateW(wForm) {

    let error = false;

    if (!this.startDate) {
      this.date3Error1 = true;
      error = true;
    } else if (!(this.startDate instanceof Object)) {
      this.date3Error2 = true;
      error = true;
    }

    if (!this.endDate) {
      this.date4Error1 = true;
      error = true;
    } else if (!(this.endDate instanceof Object)) {
      this.date4Error2 = true;
      error = true;
    }

    if (error) {
      return false;
    }

    return wForm.form.valid;
  }

  reserve() {

    this.reservation.startDate = this.getTimestamp(this.reserveStartDate);
    this.reservation.endDate = this.getTimestamp(this.reserveEndDate);

    // Chech date range
    if (!(this.getTimestamp(this.reserveStartDate) < this.getTimestamp(this.reserveEndDate))) {
      this.toastr.error('End date must be after start date');
      return;
    }

    const singleRes = new SingleReservation();

    singleRes.from = this.reservation.startDate;
    singleRes.to = this.reservation.endDate;
    singleRes.priceType = this.reservation.priceType;

    for (const ri of this.reservation.roomsInfo) {
      singleRes.beds = ri.beds;
      singleRes.noRooms = ri.noRooms;
      singleRes.kids = ri.kids;

      this.web3j.reserve(this.contractID, this.repr.id, singleRes).subscribe(data => {
        // alert('reserved')
        this.toastr.success('Reserved');
        // this.populate();
      }, error => console.log(error));

    }
  }

  withdraw(id: number) {

    this.period.startDate = this.getTimestamp(this.startDate);
    this.period.endDate = this.getTimestamp(this.endDate);

    if (!(this.period.startDate < this.period.endDate)) {
      this.toastr.error('End date must be after start date');
      return;
    }

    this.web3j.withdraw(id, this.repr.id, this.period).subscribe(data => {
      this.toastr.success('Withdrawed')
      // this.populate();
    }, error => console.log(error));
  }

  break(id: number) {
    this.web3j.break(id, this.repr.id, this.repr.type).subscribe(data => {
      this.toastr.success(data);
      this.populate();
    }, error => console.log(error));
  }

  debug() {
    this.toastr.success(this.repr.id + '');
    this.toastr.success(this.contractsCOW[0].agencyRepr + '');
    this.toastr.success(this.contractsCOW[1].agencyRepr + '');
  }

  private getTimestamp(dateStruct: NgbDateStruct) {

    if (!dateStruct) {
      return 0;
    }

    const date = new Date();
    date.setFullYear(dateStruct.year)
    date.setMonth(dateStruct.month-1)
    date.setDate(dateStruct.day)

    date.setHours(12);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);

    return date.getTime();
  }

  openReserveModal(cid: number) {
    this.contractID = cid;
  }

  dp1Toggle(datePicker: any) {
    datePicker.toggle();
    this.date1Error1 = false;
    this.date1Error2 = false;
  }

  dp2Toggle(datePicker: any) {
    datePicker.toggle();
    this.date2Error1 = false;
    this.date2Error2 = false;
  }

  dp3Toggle(datePicker: any) {
    datePicker.toggle();
    this.date3Error1 = false;
    this.date3Error2 = false;
  }

  dp4Toggle(datePicker: any) {
    datePicker.toggle();
    this.date4Error1 = false;
    this.date4Error2 = false;
  }

  ch() {
    this.date1Error1 = false;
    this.date1Error2 = false;
  }

  ch2() {
    this.date2Error1 = false;
    this.date2Error2 = false;
  }

  ch3() {
    this.date3Error1 = false;
    this.date3Error2 = false;
  }

  ch4() {
    this.date4Error1 = false;
    this.date4Error2 = false;
  }

  cbChange() {
    this.cbError = false;
  }

  openListModal(contractId: number) {
    this.contractID = contractId;

    this.web3j.getReservations(contractId).subscribe( data => {
      this.reservationsDTO = data;

      this.reservationsDTO = this.reservationsDTO.sort((a,b) => {
        if (a.from > b.from) {
          return 1;
        }

        if (a.from < b.from) {
          return -1;
        }

        return 0;

      });

      /*for (let i=0; i<20; ++i) {
        this.reservationsDTO.push(new ReservationDTO());
      }*/
    }, error => console.log(error));

  }

  verify(reservationId: number, beds: number) {

    this.web3j.verifyReservation(this.contractID, reservationId, beds, this.repr.id).subscribe( data => {
      this.toastr.success(data);

      this.web3j.getReservations(this.contractID).subscribe( list => {
        this.reservationsDTO = list;

        this.reservationsDTO = this.reservationsDTO.sort((a,b) => {
          if (a.from > b.from) {
            return 1;
          }

          if (a.from < b.from) {
            return -1;
          }

          return 0;

        });

        this.populate();

      }, error => console.log(error));

    }, error => console.log(error));

  }


  openWithdrawalsList(contractId: number) {
    this.contractID = contractId;

    this.web3j.getWithdrawals(contractId).subscribe( data => {
      this.withdrawalsDTO = data;

      this.withdrawalsDTO = this.withdrawalsDTO.sort((a,b) => {
        if (a.startDate > b.startDate) {
          return 1;
        }

        if (a.startDate < b.startDate) {
          return -1;
        }

        return 0;

      });

    }, error => console.log(error));


  }

  validateSendFunds(forma: any) {
    return forma.form.valid;
  }

  sendFundsMethod(contractId: number) {

    this.web3j.sendFunds(contractId, this.repr.id, this.fundsValue).subscribe( data => {
      this.toastr.success(data);
      this.populate();
      this.fundsValue = 0;
    }, error => console.log(error));


  }

}
