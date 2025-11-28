package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import java.util.List;


@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String version;

    /** Link to Provider entity. */
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    private String license;

    private String beliefs;

    private String intentions;

    private String desires;

    private String role;

    private String objective;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<IOModality> ioModalities;

    private String backStory;

    @Transient
    private List<Skill> skills;

    @Transient
    private List<Tool> exploits;

    @Transient
    private List<Memory> has;

    @Transient
    private List<InteractionProtocol> supports;

    @Transient
    private List<AgentConsumption> consumptions;

    public Agent() {
        // JPA
    }

    public Agent(String id,
                 String name,
                 String description,
                 String version,
                 Provider provider,
                 String license,
                 String beliefs,
                 String intentions,
                 String desires,
                 String role,
                 String objective,
                 List<IOModality> ioModalities,
                 String backStory,
                 List<Skill> skills,
                 List<Tool> exploits,
                 List<Memory> has,
                 List<InteractionProtocol> supports,
                 List<AgentConsumption> consumptions) {
            this.id = id;
        this.name = name;
        this.description = description;
        this.version = version;
    this.provider = provider;
        this.license = license;
        this.beliefs = beliefs;
        this.intentions = intentions;
        this.desires = desires;
        this.role = role;
        this.objective = objective;
        this.ioModalities = ioModalities;
        this.backStory = backStory;
        this.skills = skills;
        this.exploits = exploits;
        this.has = has;
        this.supports = supports;
        this.consumptions = consumptions;
    }

    // Keep record-like accessor methods so existing code that calls agent.name() keeps working.
    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public Provider provider() { return provider; }
    public String license() { return license; }
    public String beliefs() { return beliefs; }
    public String intentions() { return intentions; }
    public String desires() { return desires; }
    public String role() { return role; }
    public String objective() { return objective; }
    public List<IOModality> ioModalities() { return ioModalities; }
    public String backStory() { return backStory; }
    public List<Skill> skills() { return skills; }
    public List<Tool> exploits() { return exploits; }
    public List<Memory> has() { return has; }
    public List<InteractionProtocol> supports() { return supports; }
    public List<AgentConsumption> consumptions() { return consumptions; }

    // Standard getters/setters for frameworks and JPA
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    public String getBeliefs() { return beliefs; }
    public void setBeliefs(String beliefs) { this.beliefs = beliefs; }
    public String getIntentions() { return intentions; }
    public void setIntentions(String intentions) { this.intentions = intentions; }
    public String getDesires() { return desires; }
    public void setDesires(String desires) { this.desires = desires; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }
    public List<IOModality> getIoModalities() { return ioModalities; }
    public void setIoModalities(List<IOModality> ioModalities) { this.ioModalities = ioModalities; }
    public String getBackStory() { return backStory; }
    public void setBackStory(String backStory) { this.backStory = backStory; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public List<Tool> getExploits() { return exploits; }
    public void setExploits(List<Tool> exploits) { this.exploits = exploits; }
    public List<Memory> getHas() { return has; }
    public void setHas(List<Memory> has) { this.has = has; }
    public List<InteractionProtocol> getSupports() { return supports; }
    public void setSupports(List<InteractionProtocol> supports) { this.supports = supports; }
    public List<AgentConsumption> getConsumptions() { return consumptions; }
    public void setConsumptions(List<AgentConsumption> consumptions) { this.consumptions = consumptions; }
}