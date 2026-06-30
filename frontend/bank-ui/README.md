# BankUi

A simple banking web application built with **Angular 21** and **Spring Boot**.

## Features

### Home Page

* Displays all user accounts
* Shows account balance and currency
* Navigate to account details

### Account Overview

* Account information
* Infinite scrolling transaction history (Spring Page, frontend consumes paginated REST endpoints with infinite scrolling)
* Historic account balance chart (Chart.js)
* Export transaction summary as PDF
* Navigate to transaction details

### Transaction Details

* Complete transaction information
* Back navigation to the related account

## Technologies

* Angular 21
* Angular Material
* NgRx Signal Store
* Chart.js

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.17.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
