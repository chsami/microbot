import ogImageSrc from "@images/social.png";

export const SITE = {
  title: "Microbot Docs",
  tagline: "The best open source client on the market, check out our github!",
  description: "Microbot is the golden standard of open source automated clients based on runelite. It uses a plugin system to enable scripting. Check out our youtube channel for examples of our scripts",
  description_short: "Microbot offers top-tier software tools and expert services to meet all your project needs.",
  url: "https://microbot.kbve.com",
  author: "Microbot Team",
};

export const SEO = {
  title: SITE.title,
  description: SITE.description,
  structuredData: {
    "@context": "https://schema.org",
    "@type": "WebPage",
    inLanguage: "en-US",
    "@id": SITE.url,
    url: SITE.url,
    name: SITE.title,
    description: SITE.description,
    isPartOf: {
      "@type": "WebSite",
      url: SITE.url,
      name: SITE.title,
      description: SITE.description,
    },
  },
};

export const OG = {
  locale: "en_US",
  type: "website",
  url: SITE.url,
  title: `${SITE.title}: : Open Source Client`,
  description: "Microbot is an opensource automated oldschool runescape client based on runelite. It uses a plugin system to enable scripting. Here is a youtube channel showing off some of the scripts",
  image: ogImageSrc,
};
