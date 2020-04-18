import { Subject, Subscription } from 'rxjs';
import { Router, ActivatedRoute } from '@angular/router';
import { Web3jService } from './../services/web3j.service';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { RoomsInfo } from './../RoomsInfo';
import { HotelService } from './../services/hotel.service';
import { OrganizationService } from './../services/organization.service';
import { Representative } from './../Representative';
import { AuthService } from './../services/auth/auth.service';
import { Contract } from '../Contract';
import { Component, OnInit } from '@angular/core';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { Container } from '@angular/compiler/src/i18n/i18n_ast';

@Component({
  selector: 'app-propose',
  templateUrl: './propose.component.html',
  styleUrls: ['./propose.component.scss']
})
export class ProposeComponent implements OnInit {

  startDate: NgbDateStruct;
  endDate: NgbDateStruct;

  mainStartDate: NgbDateStruct;
  mainEndDate: NgbDateStruct;

  contract = new Contract();

  repr = new Representative();

  organizations = [];
  hotels = [];

  roomsInfo = new Array<RoomsInfo>();
  ri = new RoomsInfo();

  selectedHotels = [];
  dropdownSettings: IDropdownSettings;

  statusDropdownList = false;

  orgError = false;
  hotelError = false;
  roomsError = false;

  date1Error1 = false;
  date1Error2 = false;

  date2Error1 = false;
  date2Error2 = false;

  date3Error1 = false;
  date3Error2 = false;

  date4Error1 = false;
  date4Error2 = false;

  selectedOrgId = 0;

  contractId: number;

  constructor(private authService: AuthService,
              private orgService: OrganizationService,
              private hotelService: HotelService,
              private web3j: Web3jService, private route: ActivatedRoute,
              private toastr: ToastrService, private fb: FormBuilder) { }

  ngOnInit() {
    this.authService.checkLoggedIn();
    this.authService.isUserLoggedIn.next(true);

    this.repr = this.authService.getUser();

    if (this.repr.type === 'Agency') {
      // get acoomodations
      this.orgService.getAccomodations().subscribe(data => {
        this.organizations = data;
      }, error => console.log(error));
    } else {
      // get agencies
      this.orgService.getAgencies().subscribe(data => {
        this.organizations = data;
      }, error => console.log(error));

      this.populateHotels(this.repr.orgId, []);
    }

    this.dropdownSettings = {
      singleSelection: false,
      idField: 'id',
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'Unselect All',
      // itemsShowLimit: 3,
      allowSearchFilter: true
    };

    this.contract.someContrains[0] = 0;
    this.contract.someContrains[1] = 0;

    const ID = 'id';
    this.contractId = this.route.snapshot.params[ID];
    if (this.contractId) {
      this.web3j.getContractInfo(this.contractId).subscribe( res => {
        this.startDate = this.getDateStruct(res.startDate);
        this.endDate = this.getDateStruct(res.endDate);
        this.mainStartDate = this.getDateStruct(res.someContrains[4]);
        this.mainEndDate = this.getDateStruct(res.someContrains[5]);
        this.roomsInfo = this.parseRoomsInfo(res.roomsInfo);
        this.contract = res as Contract;
        this.contract.someContrains[0] = 0;
        this.contract.someContrains[1] = 0;
        this.contract.id = this.contractId;
        if (this.repr.type === 'Agency') {
          this.selectedOrgId = res.accId;
          this.statusDropdownList = true;
          this.populateHotels(res.accId, res.hotels);
        } else {
          this.selectedOrgId = res.agId;
          // this.statusDropdownList = true;
          this.populateHotels(this.repr.orgId, res.hotels);
        }
      }, error => console.log(error));
    }

  }

  private parseRoomsInfo(info: any[]): RoomsInfo[] {

    const ris = new Array<RoomsInfo>();

    for (let i=1; i<info.length; ++i) {
      if (info[i] !== 0) {
        const ri = new RoomsInfo();
        ri.beds = i;
        ri.noRooms = info[i];
        ris.push(ri);
      }
    }

    return ris;

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

  validate(form: any, form2: any) {

    form2.submitted = false;

    let error = false;

    if (!this.startDate) {
      this.date1Error1 = true;
      error = true;
    } else if (!(this.startDate instanceof Object)) {
      this.date1Error2 = true;
      error = true;
    }

    if (!this.endDate) {
      this.date2Error1 = true;
      error = true;
    } else if (!(this.endDate instanceof Object)) {
      this.date2Error2 = true;
      error = true;
    }

    if (!this.mainStartDate) {
      this.date3Error1 = true;
      error = true;
    } else if (!(this.mainStartDate instanceof Object)) {
      this.date3Error2 = true;
      error = true;
    }

    if (!this.mainEndDate) {
      this.date4Error1 = true;
      error = true;
    } else if (!(this.mainEndDate instanceof Object)) {
      this.date4Error2 = true;
      error = true;
    }

    if (this.repr.type === 'Agency') {
      if (this.contract.accId === -1) {
        // this.toastr.error('Please select other organization');
        this.orgError = true;
        error = true;
      } else {
        if (this.selectedHotels.length === 0) {
          // this.toastr.error('Please select at least one accomodation object');
          this.hotelError = true;
          error = true;
        }
      }
    }

    if (this.roomsInfo.length === 0) {
      // this.toastr.error('Please select at least one room');
      error = true;
      this.roomsError = true;
    }

    if (error) {
      return false;
    }

    return form.form.valid;
  }

  submit() {

    if (this.repr.type === 'Agency') {
      this.contract.someContrains[0] = this.repr.id;
    } else {
      this.contract.someContrains[1] = this.repr.id;
    }

    this.contract.hotels = [];
    this.selectedHotels.forEach(item => {
      this.contract.hotels.push(item.id);
    });

    this.contract.startDate = this.getTimestamp(this.startDate);
    this.contract.endDate = this.getTimestamp(this.endDate);

    const today = new Date();
    if (this.contract.startDate*1000 < today.getTime()) {
      this.toastr.error('Start date can\'t be in the past');
      return;
    }

    if (!(this.contract.startDate < this.contract.endDate)) {
      this.toastr.error('End date must be after start date');
      return;
    }

    this.contract.someContrains[4] = this.getTimestamp(this.mainStartDate);
    this.contract.someContrains[5] = this.getTimestamp(this.mainEndDate);

    if (!(this.contract.someContrains[4] < this.contract.someContrains[5])) {
      this.toastr.error('Main season end date must be after start date');
      return;
    }

    if (this.contract.someContrains[4] < this.contract.startDate || this.contract.someContrains[5] > this.contract.endDate) {
      this.toastr.error('Main season must be in chosen period');
      return;
    }

    this.roomsInfo = this.roomsInfo.sort((a,b) => {
      if (a.beds > b.beds) {
        return 1;
      }

      if (a.beds < b.beds) {
        return -1;
      }

      return 0;

    });

    const maxEl = this.roomsInfo[this.roomsInfo.length-1].beds;

    this.contract.roomsInfo = new Array<number>(maxEl);
    let totalBeds = 0;
    for (let i=1; i<=maxEl; ++i) {
      const nr = this.getRoomsForBeds(i);
      this.contract.roomsInfo[i] = nr;
      totalBeds += i * nr;
    }

    this.contract.roomsInfo[0] = totalBeds;

    // this.contract.someContrains[0];
    // this.contract.someContrains[1];

    this.web3j.proposeContract(this.contract).subscribe(res => {
      this.toastr.success('Proposal sent');
      // this.toastr.success('Proposed new contract on address ' + res);
      // alert(res);
    }, err => console.log(err))

    // this.contract = new Contract();
  }

  private getRoomsForBeds(beds: number) {

    for (const ri of this.roomsInfo) {
      if (ri.beds === beds) {
        return ri.noRooms;
      }
    }

    return 0;
  }

  onItemSelect(item: any) {
    this.hotelError = false;
    console.log(item);
  }
  onSelectAll(items: any) {
    this.hotelError = false;
    console.log(items);
  }

  onOrgChange(option) {

    this.orgError = false;
    this.hotelError = false;

    if (this.repr.type === 'Agency') {
      const orgSelect = this.organizations[option.selectedIndex-1];
      const id = orgSelect.id;
      this.contract.accId = id;
      this.populateHotels(id, []);
    } else {
      const agSelect = this.organizations[option.selectedIndex-1];
      const id = agSelect.id;
      this.contract.agId = id;
    }
  }

  private populateHotels(id: number, selected: any[]) {
    this.hotelService.getHotelsByAcc(id).subscribe(data => {
      this.selectedHotels = [];
      this.hotels = data;
      this.statusDropdownList = true;

      for (const hotelId of selected) {
        for (const hotel of this.hotels) {
          if (hotel.id === hotelId) {
            this.selectedHotels.push(hotel);
          }
        }
      }
    }, error => console.log(error));
  }

  private getDateStruct(timestamp: number): NgbDateStruct {

    const dateStruct = {day: 0, month: 0, year: 0};
    const date = new Date(timestamp);

    dateStruct.day = date.getDate();
    dateStruct.month = date.getMonth() + 1;
    dateStruct.year = date.getFullYear();

    return dateStruct;
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

    const timestampMillis = date.getTime();
    const timestamp = timestampMillis / 1000;
    // alert (timestamp);

    return timestamp;
  }

  private sqlTimestampToNgbDateStruct(timestamp: string): NgbDateStruct {
    const dateStruct = {day: 0, month: 0, year: 0};
    let parts = timestamp.split('-');
    dateStruct.year = +parts[0];
    dateStruct.month = +parts[1];

    parts = parts[2].split('T');

    dateStruct.day = +parts[0];

    return dateStruct;

  }

  addRI(f2: any) {

    this.roomsError = false;

    let exists = false;
    this.roomsInfo.forEach(item => {
      if (item.beds === this.ri.beds) {
        item.noRooms += this.ri.noRooms;
        exists = true;
        this.ri = new RoomsInfo();
        return;
      }
    });

    if (!exists) {
      // let riTemp = new RoomsInfo();
      // riTemp.noRooms = this.ri.noRooms;
      // riTemp.beds = this.ri.beds;
      this.roomsInfo.push(this.ri);

      this.roomsInfo = this.roomsInfo.sort((a,b) => {
        if (a.beds > b.beds) {
          return 1;
        }

        if (a.beds < b.beds) {
          return -1;
        }

        return 0;

      });

      this.ri = new RoomsInfo();
    }

    f2.submitted = false;
  }

  removeRoomsInfo(rInfo: RoomsInfo) {
    for (let i=0; i<this.roomsInfo.length; ++i) {
      if (rInfo.beds === this.roomsInfo[i].beds) {
        this.roomsInfo.splice(i, 1);
        if (this.roomsInfo.length === 0) {
          this.roomsError = true;
        }
        break;
      }
    }
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

}
