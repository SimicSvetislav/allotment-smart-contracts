import { element } from 'protractor';
import { RoomsInfo } from './../RoomsInfo';
import { HotelService } from './../services/hotel.service';
import { OrganizationService } from './../services/organization.service';
import { Representative } from './../Representative';
import { AuthService } from './../services/auth/auth.service';
import { Contract } from '../Contract';
import { Component, OnInit } from '@angular/core';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { IDropdownSettings } from 'ng-multiselect-dropdown';

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

  constructor(private authService: AuthService,
              private orgService: OrganizationService,
              private hotelService: HotelService) { }

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

      // TODO: Preuzmi hotele od predstavnika smestaja
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

  }

  submit() {


    if (this.repr.type === 'Agency') {
      this.contract.agencyId = this.repr.id;
    } else {
      this.contract.acoomodationId = this.repr.id;
    }

    this.selectedHotels.forEach(item => {
      this.contract.hotels.push(item.id);
    });

    this.contract.startDate = this.getTimestamp(this.startDate);
    this.contract.endDate = this.getTimestamp(this.endDate);

    this.contract.someContrains[4] = this.getTimestamp(this.mainStartDate);
    this.contract.someContrains[5] = this.getTimestamp(this.mainEndDate);


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
    for (let i=1; i<maxEl; ++i) {
      const nr = this.getRoomsForBeds(i);
      this.contract.roomsInfo[i] = nr;
      totalBeds += i * nr;
    }

    this.contract.roomsInfo[0] = totalBeds;

    this.contract = new Contract();
  }

  private getRoomsForBeds(beds: number) {
    this.roomsInfo.forEach(item => {
      if (beds === item.beds) {
        return item.noRooms;
      }
    });

    return 0;

  }

  onItemSelect(item: any) {
    console.log(item);
  }
  onSelectAll(items: any) {
    console.log(items);
  }

  onOrgChange(option) {

    if (this.repr.type === 'Agency') {
      const orgSelect = this.organizations[option.selectedIndex-1];
      const id = orgSelect.id;
      this.contract.acoomodationId = id;
      this.hotelService.getHotelsByAcc(id).subscribe(data => {
        this.selectedHotels = [];
        this.hotels = data;
        this.statusDropdownList = true;
      }, error => console.log(error));

    }
  }

  private getTimestamp(dateStruct: NgbDateStruct) {

    if (!dateStruct) {
      return 0;
    }

    const date = new Date();
    date.setDate(dateStruct.day)
    date.setMonth(dateStruct.month-1)
    date.setFullYear(dateStruct.year)

    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);

    const timestampMillis = date.getTime();
    const timestamp = timestampMillis / 1000;
    // alert (timestamp);

    return timestamp;
  }

  addRI() {

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
      this.roomsInfo.push(this.ri);
      this.ri = new RoomsInfo();
    }
  }

  removeRoomsInfo(rInfo: RoomsInfo) {
    for (let i=0; i<this.roomsInfo.length; ++i) {
      if (rInfo.beds === this.roomsInfo[i].beds) {
        this.roomsInfo.splice(i, 1);
        break;
      }
    }
  }
}
