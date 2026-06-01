import java.util.ArrayList;
import java.util.List;

class MovieRecord {
    String genre;
    int year;
    String titleId;

    MovieRecord(String genre, int year, String titleId) {
        this.genre = genre;
        this.year = year;
        this.titleId = titleId;
    }

    @Override
    public String toString() {
        return "(" + genre + ", " + year + ", " + titleId + ")";
    }
}

class LeafNode {
    List<MovieRecord> records = new ArrayList<>();
    LeafNode next;

    void add(MovieRecord record) {
        records.add(record);
    }
}

public class NetflixBPlusTreeCaseStudy {

    public static void main(String[] args) {

        System.out.println("=== Netflix B+ Tree -- Release-Year Range Scan (Movie Catalog) ===");

        // Create leaf nodes
        LeafNode leaf1 = new LeafNode();
        LeafNode leaf2 = new LeafNode();
        LeafNode leaf3 = new LeafNode();
        LeafNode leaf4 = new LeafNode();
        LeafNode leaf5 = new LeafNode();
        LeafNode leaf6 = new LeafNode();

        // Link leaf chain
        leaf1.next = leaf2;
        leaf2.next = leaf3;
        leaf3.next = leaf4;
        leaf4.next = leaf5;
        leaf5.next = leaf6;

        // Populate sample data
        leaf1.add(new MovieRecord("thriller", 2015, "TITLE_T2015A"));
        leaf1.add(new MovieRecord("thriller", 2015, "TITLE_T2015B"));

        leaf2.add(new MovieRecord("thriller", 2016, "TITLE_T2016A"));
        leaf2.add(new MovieRecord("thriller", 2016, "TITLE_T2016B"));

        leaf3.add(new MovieRecord("thriller", 2017, "TITLE_T2017"));
        leaf3.add(new MovieRecord("thriller", 2018, "TITLE_T2018"));

        leaf4.add(new MovieRecord("thriller", 2019, "TITLE_T2019"));
        leaf4.add(new MovieRecord("thriller", 2020, "TITLE_T2020"));

        leaf5.add(new MovieRecord("thriller", 2021, "TITLE_T2021A"));
        leaf5.add(new MovieRecord("thriller", 2021, "TITLE_T2021B"));

        leaf6.add(new MovieRecord("thriller", 2022, "TITLE_T2022"));
        leaf6.add(new MovieRecord("thriller", 2023, "TITLE_T2023"));

        int lo = 2015;
        int hi = 2022;

        System.out.println("\n==============================================================");
        System.out.println("B+ TREE RANGE QUERY");
        System.out.println("genre='thriller', release_year IN [2015, 2022]");
        System.out.println("==============================================================");

        // Phase 1
        System.out.println("\n--- PHASE 1: Tree Descent (root -> internal -> leaf) ---");

        System.out.println("[Read #1] ROOT [2005 | 2015]");
        System.out.println("          lo=2015 -> route to right subtree");

        System.out.println("[Read #2] INTERNAL [2010 | 2015]");
        System.out.println("          first matching leaf = Leaf-1");

        System.out.println("[Read #3] LEAF-1 (2015)");
        System.out.println("          scan entries >= 2015");

        // Phase 2
        System.out.println("\n--- PHASE 2: Leaf Chain Walk (.next pointer) ---");

        List<MovieRecord> result = new ArrayList<>();

        LeafNode current = leaf1;
        int pageRead = 3;

        while (current != null) {

            if (current != leaf1) {
                pageRead++;
                System.out.println("[Read #" + pageRead +
                        "] Next Leaf via .next pointer");
            }

            for (MovieRecord record : current.records) {

                if (record.year >= lo && record.year <= hi) {
                    result.add(record);
                }

                if (record.year > hi) {
                    current = null;
                    break;
                }
            }

            if (current != null)
                current = current.next;
        }

        // Query Results
        System.out.println("\n==============================================================");
        System.out.println("QUERY RESULT SUMMARY");
        System.out.println("==============================================================");

        System.out.println("Titles in release_year range [2015, 2022]:\n");

        for (MovieRecord movie : result) {
            System.out.println(movie);
        }

        System.out.println("\nTotal titles returned : " + result.size());

        System.out.println("\nPAGE READ BREAKDOWN");
        System.out.println("Tree Descent Reads : 3");
        System.out.println("Leaf Chain Reads   : 5");
        System.out.println("Total Page Reads   : 8");

        // Performance Analysis
        System.out.println("\n==============================================================");
        System.out.println("B+ TREE PERFORMANCE ANALYSIS");
        System.out.println("==============================================================");

        System.out.println("Catalog size               : 5,000,000 titles");
        System.out.println("Leaf capacity              : 160 tuples/page");
        System.out.println("Tree height                : ~4 levels");

        int descentCost = 3 * 100;
        int leafCost = 5 * 10;
        int totalLatency = descentCost + leafCost;

        System.out.println("Tree descent cost          : "
                + descentCost + " microseconds");

        System.out.println("Leaf chain cost            : "
                + leafCost + " microseconds");

        System.out.println("Estimated query latency    : "
                + totalLatency + " microseconds");

        System.out.println("\nTypical binge-search query:");
        System.out.println("3200 results -> 20 leaves");
        System.out.println("Latency = 3×100 + 20×10 = 500 microseconds");

        // Hash Index Analysis
        System.out.println("\n==============================================================");
        System.out.println("HASH INDEX ANALYSIS");
        System.out.println("==============================================================");

        System.out.println("92% queries = Range Scans");
        System.out.println("8% queries  = Point Lookups");

        System.out.println("\nVERDICT:");
        System.out.println("Hash Index NOT Recommended");
        System.out.println("- Cannot support range scans");
        System.out.println("- Benefits only 8% of workload");
        System.out.println("- Adds dual-write overhead");

        System.out.println("\n==============================================================");
        System.out.println("TIME COMPLEXITY");
        System.out.println("==============================================================");

        System.out.println("B+ Tree Insert/Delete : O(log n)");
        System.out.println("B+ Tree Point Lookup  : O(log n)");
        System.out.println("B+ Tree Range Scan    : O(log n + k/160)");
        System.out.println("Hash Lookup           : O(1)");
    }
}