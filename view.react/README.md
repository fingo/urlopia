# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

## No-auth mode

There is option to configure no-auth mode. If this is chosen there is no msal components and any form of authentication.
This should be run only with backend with `ad.configuration.enabled = false` properties [check details](../readme.md#application-without-active-directory)

To activate this mode set `REACT_APP_AUTH_MODE` environment variables to `NO-AUTH`. 
Important! This mode required to have in database active user with id = 1 (this is default user, before any is selected)

In this mode permissions are still used -> you can switch users without authentication, but they still have to be active
and have required permissions on db.