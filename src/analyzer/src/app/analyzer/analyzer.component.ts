import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleChartComponent }         from 'ng2-google-charts';

@Component({
  selector: 'app-analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {

  @ViewChild(GoogleChartComponent) cchart: GoogleChartComponent;
  public line_ChartData: any[] = null;
  public line_ChartOptions = {
    chartType: 'LineChart',
    dataTable: null,
    options: {'title': 'Tasks'},
  };

  constructor() { }

  ngOnInit() {

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

  myfunction() {
    let googleChartWrapper = this.cchart.wrapper;

    //force a redraw
    this.cchart.redraw();
  }

}
