import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';

import { GenericStat } from './data-model';
import { MessageService } from './message.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class GenericStatService {

  private GenericStatesUrl = 'api/heroes';  // URL to web api

  constructor(
    private http: HttpClient,
    private messageService: MessageService) { }

  /** GET GenericStats from the server */
  getStats (): Observable<GenericStat[]> {
    return this.http.get<GenericStat[]>(this.baseUrl)
      .pipe(
        tap(heroes => this.log(`fetched stats`)),
        catchError(this.handleError('getStats', []))
      );
  }

  /** GET stat by id. Return `undefined` when id not found */
  getStatNo404<Data>(id: number): Observable<GenericStat> {
    const url = `${this.baseUrl}/?id=${id}`;
    return this.http.get<GenericStat[]>(url)
      .pipe(
        map(heroes => heroes[0]), // returns a {0|1} element array
        tap(h => {
          const outcome = h ? `fetched` : `did not find`;
          this.log(`${outcome} hero id=${id}`);
        }),
        catchError(this.handleError<GenericStat>(`getGenericStat id=${id}`))
      );
  }

  /** GET hero by id. Will 404 if id not found */
  getGenericStat(id: number): Observable<GenericStat> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.get<GenericStat>(url).pipe(
      tap(_ => this.log(`fetched hero id=${id}`)),
      catchError(this.handleError<GenericStat>(`getGenericStat id=${id}`))
    );
  }

  /* GET heroes whose name contains search term */
  searchGenericStates(term: string): Observable<GenericStat[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<GenericStat[]>(`api/heroes/?name=${term}`).pipe(
      tap(_ => this.log(`found heroes matching "${term}"`)),
      catchError(this.handleError<GenericStat[]>('searchGenericStates', []))
    );
  }

  //////// Save methods //////////

  /** POST: add a new hero to the server */
  addGenericStat (hero: GenericStat): Observable<GenericStat> {
    return this.http.post<GenericStat>(this.baseUrl, hero, httpOptions).pipe(
      tap((hero: GenericStat) => this.log(`added hero w/ id=${hero.id}`)),
      catchError(this.handleError<GenericStat>('addGenericStat'))
    );
  }

  /** DELETE: delete the hero from the server */
  deleteGenericStat (hero: GenericStat | number): Observable<GenericStat> {
    const id = typeof hero === 'number' ? hero : hero.id;
    const url = `${this.baseUrl}/${id}`;

    return this.http.delete<GenericStat>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted hero id=${id}`)),
      catchError(this.handleError<GenericStat>('deleteGenericStat'))
    );
  }

  /** PUT: update the hero on the server */
  updateGenericStat (hero: GenericStat): Observable<any> {
    return this.http.put(this.baseUrl, hero, httpOptions).pipe(
      tap(_ => this.log(`updated hero id=${hero.id}`)),
      catchError(this.handleError<any>('updateGenericStat'))
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a GenericStatService message with the MessageService */
  private log(message: string) {
    this.messageService.add('GenericStatService: ' + message);
  }
}
