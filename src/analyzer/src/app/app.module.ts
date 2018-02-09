import { NgModule }       from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { FormsModule }    from '@angular/forms';
import { HttpClientModule }    from '@angular/common/http';
import { HttpModule }                            from '@angular/http';

import { AppRoutingModule }     from './app-routing.module';

import { AppComponent }         from './app.component';

import { NavmenuComponent }     from './navmenu/navmenu.component';
import { HomeComponent }        from './home/home.component';
import { AnalyzerComponent }    from './analyzer/analyzer.component';

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,

    HttpModule
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
