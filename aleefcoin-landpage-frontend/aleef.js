import React from 'react';
import ReactDOM, {render} from 'react-dom';
import { BrowserRouter, Switch, Route, Redirect } from 'react-router-dom';

import LandPage from './src/landPage';
import PrivacyPolicy from './src/components/privacyPolicy';
import TermsOfService from './src/components/termsofservice';
import Faq from './src/components/faq';
import Success from './src/components/success';

render(<BrowserRouter>
    <Switch>
        <Redirect exact path="/" to="landpage" />
        <Route path="/landpage" component={LandPage} />
        <Route path="/privacypolicy" component={PrivacyPolicy} />
        <Route path="/termsofservice" component={TermsOfService}/>
        <Route path="/faq" component={Faq}/>
        <Route path="/sucess" component={Success}/>
    </Switch>
</BrowserRouter>, document.getElementById('app'));
