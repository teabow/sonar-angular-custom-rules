# Rules implemented

- No `jQuery` calls in controllers
- No HTML in controllers
- Files names should explicitly mention the component type (eg : `user-controller.js`)
- String constants should be defined in a provider
- `$digest` function should not be called explicitly
- [`ngAnnotate`](https://www.npmjs.com/package/gulp-ng-annotate) or `$inject` should be used  to manage injections
- Directive should be prefixed (eg : `xxAvengerProfile` for `<xx-avenger-profile> </xx-avenger-profile>`)
- Controller name should be suffixed (eg : `AvengersController`)
- Angular `$` wrapper services should be used (`$document, $window, $timeout, $interval`...)
