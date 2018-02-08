import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleChartComponent }         from 'ng2-google-charts';

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
    options: {'title': 'Tasks'},
  };

  constructor() { }

  ngOnInit() {
    this.line_ChartData = true;

    this.line_ChartOptions.dataTable = [
      ['Year', 'Sales', 'Expenses'],
      ['2004', 1000, 400],
      ['2005', 1170, 460],
      ['2006', 660, 1120],
      ['2007', 1030, 540]
    ];
    //
	  // this.line_ChartOptions =
  }

  refreshData() {
    this.line_ChartData = true;

    try {
      let googleChartWrapper = this.cchart.wrapper;

      //force a redraw
      this.cchart.redraw();
    }
    catch (Exception) {
      console.log('scope is ' + Exception);
    }
  }
}
