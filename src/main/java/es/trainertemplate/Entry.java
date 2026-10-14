package es.trainertemplate;
import jakarta.persistence.*;
@Entity @Table(name="trainer_entry") public class Entry {
 @Id public String id; @Column(nullable=false) public String ownerName; @Column(nullable=false) public String kind;
 @Column(nullable=false,length=1000000) public String payload; @Version public long version;
 protected Entry() {} Entry(String id,String owner,String kind,String payload){this.id=id;this.ownerName=owner;this.kind=kind;this.payload=payload;}
}
