
from soup import BeautifulSoup as bs
from scrapy.http import Request

from scrapy.spider import BaseSpider
#from scrapy.selector import HtmlXPathSelector

from hn.items import HnItem

class HnSpider(BaseSpider):
    name = 'hn'
    allowed_domains = []
    start_urls = ['http://news.ycombinator.com']

    def parse(self, response):
        if 'news.ycombinator.com' in response.url:
            soup = bs(response.body)
            items = [(x[0].text, x[0].get('href')) for x in
                     filter(None, [
                        x.findChildren() for x in
                        soup.findAll('td', {'class': 'title'})
                        ])]

            for item in items:
                print item
                hn_item = HnItem()
                hn_item['title'] = item[0]
                hn_item['link'] = item[1]
                try:
                    yield Request(item[1], callback=self.parse)
                except ValueError:
                    yield Request('http://news.ycombinator.com/' + items[1], callback=self.parse)

                yield hn_item

"""
    def parse(self, response):
        sel = HtmlXPathSelector(response)
        sites = sel.select('//td[@class="title"]')
        for site in sites:
            title = site.select('a/text()').extract()
            link = site.select('a/@href').extract()

            print title, link
"""

