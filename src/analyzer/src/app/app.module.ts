import { NgModule }       from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { FormsModule }    from '@angular/forms';
import { HttpClientModule }    from '@angular/common/http';

import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService }  from './in-memory-data.service';

import { AppRoutingModule }     from './app-routing.module';

import { AppComponent }         from './app.component';
// import { GenericStatService }   from './generic-stat.service';
// import { MessageService }       from './message.service';
// import { MessagesComponent }    from './messages/messages.component';
import { NavmenuComponent }     from './navmenu/navmenu.component';
import { HomeComponent }        from './home/home.component';
import { AnalyzerComponent }    from './analyzer/analyzer.component';

import { Ng2GoogleChartsModule } from 'ng2-google-charts';

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpClientModule,

    // // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
    // // and returns simulated server responses.
    // // Remove it when a real server is ready to receive requests.
    // HttpClientInMemoryWebApiModule.forRoot(
    //   InMemoryDataService, { dataEncapsulation: false }
    // ),
    Ng2GoogleChartsModule,
  ],
  declarations: [
    AppComponent,
    NavmenuComponent,
    HomeComponent,
    AnalyzerComponent
  ],
  // providers: [ HeroService, MessageService ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
