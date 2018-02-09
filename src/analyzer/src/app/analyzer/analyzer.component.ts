import { Component, OnInit, ViewChild, ElementRef, Inject } from '@angular/core';
import * as c3                                      from "c3";

import { Headers, Http }                            from '@angular/http';

import { CPUStat }                                  from '../model/cpu-stat';
import { RAMStat }                                  from '../model/ram-stat';
import { IOStat }                                   from '../model/io-stat';
import { UptimeStat }                               from '../model/uptime-stat';
import { GenericStat }                              from "../model/generic-stat";

import {CPUSTATS}                                   from "../mock-stats";


@Component({
  selector: 'app-analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  private static baseUrl = "http://localhost:8080/datamanager-web/datamanager";

  @ViewChild('dataContainer') private dataContainer: ElementRef;

  fromDate: Date;
  toDate: Date;

  scanner1: boolean = true;
  scanner2: boolean;
  scanner3: boolean;
  scanner4: boolean;
  scanner5: boolean;

  CPUStat: boolean = true;
  RAMStat: boolean;
  IOStat: boolean;
  UptimeStat: boolean;

  constructor(private http: Http) { }

  ngOnInit() {
    // this.fromDate = null;
    // this.toDate = null;
    this.refreshData();
  }

  public createPlot(chartName : string, columns : any[]) {
    return c3.generate({
       bindto: '#' + chartName,
       data: {
         x: 'Timestamp',
         columns: columns,
       },
       axis: {
         x: {
          type: 'timeseries',
          tick: {
            format: '%Y-%m-%d'
          }
        }
      }
     });
  }

  private appendContainer(displayName, chartName) {
      this.dataContainer.nativeElement.innerHTML += '<h3>' + displayName +  '</h3><br><div id="' + chartName + '"></div>';
  }

  private clearContainer() {
      this.dataContainer.nativeElement.innerHTML = "";
  }

  private retrieveData(scanner : string, type : any, from? : number, to?: number) {
    var url : string = AnalyzerComponent.baseUrl + '/topics/' + type.name + '/scanners/' + scanner;
    if (from != null && to != null ) url += '/' + from + '/' + to;
    console.log(url);
    // baseUrl + /topics/cpustat/scanners/distsystems_scanner_1/1518155143/151899143
    this.http
          .get(url)
          .subscribe(
            result => {
                this.applyData(result.json() as typeof type[], type);
            },
            error => {
              console.error(error)
            }
          );
  }

  refreshData() {
    var fromDate : number = null;
    var toDate : number = null;

    if (this.fromDate != null) fromDate = Math.round(new Date(this.fromDate).getTime()/1000);
    if (this.toDate != null) toDate = Math.round(new Date(this.toDate).getTime()/1000);

    console.log(fromDate);
    console.log(toDate);

    var scanners : Set<string> = new Set();
    var topics : Set<any> = new Set();

    if (this.scanner1) scanners.add("distsystems_scanner_1");
    if (this.scanner2) scanners.add("distsystems_scanner_2");
    if (this.scanner3) scanners.add("distsystems_scanner_3");
    if (this.scanner4) scanners.add("distsystems_scanner_4");
    if (this.scanner5) scanners.add("distsystems_scanner_5");

    if (this.CPUStat) topics.add(CPUStat);
    if (this.RAMStat) topics.add(RAMStat);
    if (this.IOStat) topics.add(IOStat);
    if (this.UptimeStat) topics.add(UptimeStat);

    this.clearContainer();

    scanners.forEach((scanner) => {
      topics.forEach((topic) => {
        this.retrieveData(scanner, topic, fromDate, toDate);
      });
    });

    // this.applyData(CPUSTATS, CPUStat);
  }

  private mergeAll(values : any[], value: any[]) {
    for(var key in value) {
        values[key].push(value[key]);
    }
  }

  applyData(data : any[], type : any) {
    // try {
      var scanners : Set<String> = new Set();
      data.filter((elem) => scanners.add(elem.scannerId));


      scanners.forEach((scanner) => {
        var chartName : string = 'chart_' + scanner;

        this.appendContainer("Scanner " + scanner + " - " + type.name, chartName);

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

        var buffer = this.createPlot(chartName, columns);
      });
    // }
    // catch (Exception) {
    //   console.log('scope is ' + Exception);
    // }
  }
}
