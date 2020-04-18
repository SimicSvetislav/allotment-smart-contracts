import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'rateFormat'
})
export class RateFormatPipe implements PipeTransform {

  transform(value: any, ...args: any[]): any {
    switch (value) {
      case 1:
        return 'Half-board';
      case 2:
        return 'Full-board';
      case 3:
        return 'Offseason';
    }
    return null;
  }

}
