import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AnalyzerComponent }    from './analyzer/analyzer.component';

const routes: Routes = [
  { path: '', redirectTo: 'analyzer', pathMatch: 'full' },
  { path: 'analyzer', component: AnalyzerComponent },
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
