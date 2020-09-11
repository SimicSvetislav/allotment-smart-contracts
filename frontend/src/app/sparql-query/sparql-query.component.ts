import { ToastrService } from 'ngx-toastr';
import { SparqlService } from './../services/sparql/sparql.service';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { Query, QueryDTO } from './../../types';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sparql-query',
  templateUrl: './sparql-query.component.html',
  styleUrls: ['./sparql-query.component.scss']
})
export class SparqlQueryComponent implements OnInit {

  query = new Query();
  view = 'form';

  constructor(private service: SparqlService, private toastr: ToastrService) { }

  ngOnInit() {
  }


  dpStartToggle(datePicker: any): void {
    datePicker.toggle();
  }

  changeStartDate(): void {

  }

  dpEndToggle(datePicker: any): void {
    datePicker.toggle();
  }

  changeEndDate(): void {

  }

  submitQuery() {

    // Provere validnosti

    if (this.query.startDate === undefined) {
      this.toastr.error('Please select start date', 'Start date is required');
      return;
    } else if (!(this.query.startDate instanceof Object)) {
      this.toastr.error('Please select valid start date', 'Start date not valid');
      return;
    }

    if (this.query.endDate === undefined) {
      this.toastr.error('Please select end date', 'End date is required');
      return;
    } else if (!(this.query.startDate instanceof Object)) {
      this.toastr.error('Please select valid start date', 'Start date not valid');
      return;
    }

    if (this.query.location === undefined || this.query.location.trim() === '') {
      this.toastr.error('Please type location', 'Location is required');
      return;
    }

    const dto = new QueryDTO();
    dto.startDate = this.getTimestamp(this.query.startDate);
    dto.endDate = this.getTimestamp(this.query.endDate);

    if (dto.startDate >= dto.endDate) {
      this.toastr.error('End date must be after start date', 'Date range not valid');
      return;
    }

    dto.location = this.query.location;

    this.service.submitQuery(dto).subscribe( res => {
      this.view = 'results';
    });

  }

  private getTimestamp(dateStruct: NgbDateStruct): number {

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

    return timestampMillis;
  }

}
