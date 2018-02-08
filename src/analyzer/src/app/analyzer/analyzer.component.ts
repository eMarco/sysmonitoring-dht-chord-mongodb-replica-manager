import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleChartComponent }         from 'ng2-google-charts';
import { CPUStat }                      from '../model/cpu-stat';

// import * as Plotly                      from 'plotly.js';
// import {Config, Data, Layout}           from 'plotly.js';

import {CPUSTATS} from "../mock-stats";
import { GenericStat } from "../model/generic-stat";

@Component({
  selector: 'app-analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {

  @ViewChild('cchart') cchart: GoogleChartComponent;
  public line_ChartData: boolean = false;
  public line_ChartOptions = {
    chartType: 'LineChart',
    dataTable: null,
    options: {'title': 'CPUStats'},
  };
  public test = "ASD";

  constructor() { }

  ngOnInit() {
    this.line_ChartData = true;

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
    //
	  // this.line_ChartOptions =

    // const data: Plotly.BarData[] = [
    //   {
    //     x: ['giraffes', 'orangutans', 'monkeys'],
    //     y: [20, 14, 23],
    //     type: 'bar'
    //   }
    // ];
    //
    // test = Plotly.newPlot('test', data);
  }
  refreshData() {
    this.applyData(CPUSTATS as CPUStat[], CPUStat);
  }

  applyData(data : any[], type : any) {
    try {
      this.line_ChartData = true;

      this.line_ChartOptions.dataTable = [ type.label ];

      data.map((value) => {
        this.line_ChartOptions.dataTable.push(type.toArray(value));
      });

      let googleChartWrapper = this.cchart.wrapper;


      //force a redraw
      this.cchart.redraw();
    }
    catch (Exception) {
      console.log('scope is ' + Exception);
    }
  }
}
