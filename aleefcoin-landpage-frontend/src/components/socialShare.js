import React from 'react';

export default class SocialShare extends React.Component {
	render() {
		return (
			<div>
				<aside className="shareSocialBg">
					<ul>
						<li className="share"><a ><img src="src/public/img/sharing.png" /></a></li>
						<li className="facebook"><a className="w-inline-block social-share-btn fb" href="https://www.facebook.com/sharer/sharer.php?u=&t=" title="Share on Facebook" target="_blank" onClick={() => "window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(document.URL) + '&t=' + encodeURIComponent(document.URL)); return false;"}><img src="src/public/img/facebook.png" /></a></li>
						<li className="twitter"><a className="w-inline-block social-share-btn tw" href="https://twitter.com/intent/tweet?" target="_blank" title="Tweet" onClick={() => "window.open('https://twitter.com/intent/tweet?text=%20Check%20up%20this%20awesome%20content' + encodeURIComponent(document.title) + ':%20 ' + encodeURIComponent(document.URL)); return false;"}><img src="src/public/img/twitter.png" /></a></li>
						<li className="googlePlus"><a className="w-inline-block social-share-btn gplus" href="https://plus.google.com/share?url=" target="_blank" title="Share on Google+" onClick={() => "window.open('https://plus.google.com/share?url=' + encodeURIComponent(document.URL)); return false;"}><img src="src/public/img/google-plus.png" /></a></li>
						<li className="linked"><a className="w-inline-block social-share-btn lnk" href="http://www.linkedin.com/shareArticle?mini=true&url=&title=&summary=&source=" target="_blank" title="Share on LinkedIn" onClick={() => "window.open('http://www.linkedin.com/shareArticle?mini=true&url=' + encodeURIComponent(document.URL) + '&title=' + encodeURIComponent(document.title)); return false;"}><img src="src/public/img/linkedin.png" /></a></li>
						<li className="reddit"><a className="w-inline-block social-share-btn redd" href="http://www.reddit.com/submit?url=&title=" target="_blank" title="Submit to Reddit" onClick={() => "window.open('http://www.reddit.com/submit?url=' + encodeURIComponent(document.URL) + '&title=' + encodeURIComponent(document.title)); return false;"}> <img src="src/public/img/reddit.png" /></a></li>
					</ul>
				</aside>
			</div>
		)
	}
}