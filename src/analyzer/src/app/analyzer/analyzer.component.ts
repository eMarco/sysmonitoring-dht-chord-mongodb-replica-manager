import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { GoogleChartComponent }         from 'ng2-google-charts';
import * as c3 from "c3";

import { CPUStat }                      from '../model/cpu-stat';
import { GenericStat } from "../model/generic-stat";

import {CPUSTATS} from "../mock-stats";


@Component({
  selector: 'app-analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {

  // @ViewChild('cchart') cchart: GoogleChartComponent;
  // public line_ChartData: boolean = false;
  // public line_ChartOptions = {
  //   chartType: 'LineChart',
  //   dataTable: null,
  //   options: {'title': 'CPUStats'},
  // };
  constructor() { }

  ngOnInit() {
    this.refreshData();
  }

  public createGraph(chartName : string, columns : any[]) {
    return c3.generate({
       bindto: '#' + chartName,
       data: {
         x: 'Timestamp',
         columns: columns,
       },
      //  axis: {
      //    x: {
      //     type: 'timeseries',
      //     tick: {
      //       format: '%Y-%m-%d'
      //     }
      //   }
      // }
     });
  }
  @ViewChild('dataContainer') private dataContainer: ElementRef;

  private appendContainer(chartName) {
      this.dataContainer.nativeElement.innerHTML += '<div id="' + chartName + '"></div>';
  }

  private clearContainer() {
      this.dataContainer.nativeElement.innerHTML = "";
  }

  refreshData() {
    this.applyData(CPUSTATS, CPUStat);
  }

  private mergeAll(values : any[], value: any[]) {
    for(var key in value) {
        values[key].push(value[key]);
    }
  }

  applyData(data : any[], type : any) {
    // try {
      this.clearContainer();

      var scanners : Set<String> = new Set();
      data.filter((elem) => scanners.add(elem.scannerId));


      scanners.forEach((scanner) => {
        var chartName : string = 'chart_' + scanner;
        console.log("Creating graph " + chartName );

        this.appendContainer(chartName);
        // this.loadData();

        var columns = new Array<Array<any>>();
        for (var label in type.labels) {
          columns.push(new Array<any>(type.labels[label]));
        }

        data  .filter((elem) => elem.scannerId == scanner)        // filter for selected scanner
              .sort((e1, e2) => e1.timestamp - e2.timestamp)      // sort by timestamp
              .map((measure) => {

              this.mergeAll(columns, type.toArray(measure));

              // x_col.push(new Date(measure.timestamp));
        });
        console.log("Creating graph " + chartName);
        console.log(columns);

        var buffer = this.createGraph(chartName, columns);
      });


      data.map((value) => {
        // this.line_ChartOptions.dataTable.push(type.toArray(value));
      });
    // }
    // catch (Exception) {
    //   console.log('scope is ' + Exception);
    // }
  }
}
