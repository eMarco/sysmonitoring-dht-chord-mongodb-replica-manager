import { Component, OnInit, ViewChild } from '@angular/core';
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
    var chart = c3.generate({
       bindto: '#chart',
       data: {
         columns: [
           ['data1', 30, 200, 100, 400, 150, 250],
           ['data2', 50, 20, 10, 40, 15, 25]
         ]
       }
     });
    // Plotly.newPlot('test', asd);

    // this.line_ChartData = true;

    var test = [{ usage: 0.5, timestamp: 4, scannerId: "asd", key: { key:"1699d6b5508374cf2becc8778548b263271da293"} }]

    var type = CPUStat;

    this.applyData(test as CPUStat[], CPUStat);

    // this.line_ChartOptions.dataTable = [
    //   ['Year', 'Sales', 'Expenses'],
    //   ['2004', 1000, 400],
    //   ['2005', 1170, 460],
    //   ['2006', 660, 1120],
    //   ['2007', 1030, 540]
    // ];
  }
  refreshData() {
    this.applyData(CPUSTATS as CPUStat[], CPUStat);
  }

  applyData(data : any[], type : any) {
    try {
      // this.line_ChartData = true;
      //
      // this.line_ChartOptions.dataTable = [ type.label ];


      data.map((value) => {
        // this.line_ChartOptions.dataTable.push(type.toArray(value));
      });

      // let googleChartWrapper = this.cchart.wrapper;
      //
      //
      // //force a redraw
      // this.cchart.redraw();
    }
    catch (Exception) {
      console.log('scope is ' + Exception);
    }
  }
}
