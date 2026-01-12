package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
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

    @Column(columnDefinition = "text")
    private String description;
    private String version;
    /** Link to Provider entity. */
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    private String license;
    @Column(columnDefinition = "text")
    private String beliefs;
    @Column(columnDefinition = "text")
    private String intentions;
    @Column(columnDefinition = "text")
    private String desires;
    private String role;
    @Column(columnDefinition = "text") 
    private String objective;
    private String llangfuseSecretKey;
    private String llangfuseUrl;
    private String llangfusePublicKey;
    private String llangfuseProjectName;
    public String getLlangfuseUrl() {
        return llangfuseUrl;
    }

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<IOModality> ioModalities;
    
    @Column(columnDefinition = "text")
    private String backStory;

    @Column(columnDefinition = "text")
    private String a2aAgentCardUrl;

    @Embedded
    private Deployment deployment;

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

    public Agent(String name,
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
                 String a2aAgentCardUrl,
                 Deployment deployment,
                 List<Skill> skills,
                 List<Tool> exploits,
                 List<Memory> has,
                 List<InteractionProtocol> supports,
                 List<AgentConsumption> consumptions) {
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
        this.a2aAgentCardUrl = a2aAgentCardUrl;
        this.deployment = deployment;
        this.skills = skills;
        this.exploits = exploits;
        this.has = has;
        this.supports = supports;
        this.consumptions = consumptions;
    }


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
    public String getA2aAgentCardUrl() { return a2aAgentCardUrl; }
    public void setA2aAgentCardUrl(String a2aAgentCardUrl) { this.a2aAgentCardUrl = a2aAgentCardUrl; }
    public Deployment getDeployment() { return deployment; }
    public void setDeployment(Deployment deployment) { this.deployment = deployment; }
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
    public void setLlangfuseUrl(String llangfuseUrl) {this.llangfuseUrl = llangfuseUrl;}    
    public String getLlangfuseProjectName() {return llangfuseProjectName;}
    public void setLlangfuseProjectName(String llangfuseProjectName) {this.llangfuseProjectName = llangfuseProjectName;    }
    public String getLlangfusePublicKey() {return llangfusePublicKey;  }
    public void setLlangfusePublicKey(String llangfusePublicKey) {this.llangfusePublicKey = llangfusePublicKey;}
    public String getLlangfuseSecretKey() {return llangfuseSecretKey;}
    public void setLlangfuseSecretKey(String llangfuseSecretKey) {this.llangfuseSecretKey = llangfuseSecretKey;}
}