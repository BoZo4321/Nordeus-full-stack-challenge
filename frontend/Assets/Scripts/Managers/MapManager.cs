using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MapManager : MonoBehaviour
{
    [Header("Hero Info")]
    [SerializeField] private Text heroNameText;
    [SerializeField] private Text heroLevelText;
    [SerializeField] private Text heroHpText;
    [SerializeField] private Text heroXpText;

    [Header("Encounters - assign 5 buttons in order")]
    [SerializeField] private Button[] encounterButtons;
    [SerializeField] private Text[] encounterNameTexts;
    [SerializeField] private Text[] encounterStatusTexts;

    [Header("Move Management")]
    [SerializeField] private Button openMoveManagementButton;
    [SerializeField] private GameObject moveManagementPanel;

    [Header("Navigation")]
    [SerializeField] private Button exitButton;

    private StartRunResponse _run;

    void Start()
    {
        _run = GameManager.Instance.CurrentRun;
        RefreshHeroInfo();
        RefreshEncounters();
        openMoveManagementButton.onClick.AddListener(() => moveManagementPanel.SetActive(true));

        if (exitButton != null)
            exitButton.onClick.AddListener(() => Application.Quit());
    }

    void OnEnable()
    {
        if (GameManager.Instance?.CurrentRun != null)
        {
            _run = GameManager.Instance.CurrentRun;
            RefreshHeroInfo();
            RefreshEncounters();
        }
    }

    private void RefreshHeroInfo()
    {
        var hero = _run.hero;
        heroNameText.text = hero.name;
        heroLevelText.text = "Level " + hero.level;
        heroHpText.text = "HP: " + hero.stats.maxHealth + " / " + hero.stats.maxHealth;
        heroXpText.text = "XP: " + hero.xp;
    }

    private void RefreshEncounters()
    {
        for (int i = 0; i < encounterButtons.Length; i++)
        {
            if (i >= _run.encounters.Length) break;

            var enc = _run.encounters[i];
            encounterNameTexts[i].text = enc.monsterName;

            switch (enc.status)
            {
                case "AVAILABLE":
                    encounterButtons[i].interactable = true;
                    encounterStatusTexts[i].text = "Fight!";
                    break;
                case "COMPLETED":
                    encounterButtons[i].interactable = true;
                    encounterStatusTexts[i].text = "Defeated";
                    break;
                case "LOCKED":
                    encounterButtons[i].interactable = false;
                    encounterStatusTexts[i].text = "Locked";
                    break;
            }

            var monsterId = enc.monsterId;
            encounterButtons[i].onClick.RemoveAllListeners();
            encounterButtons[i].onClick.AddListener(() => StartBattle(monsterId));
        }
    }

    private void StartBattle(string monsterId)
    {
        StartCoroutine(ApiClient.Instance.Post<StartBattleResponse>(
            "/runs/" + _run.runId + "/battles",
            new StartBattleRequest { monsterId = monsterId },
            response =>
            {
                GameManager.Instance.CurrentBattleId = response.battleId;
                GameManager.Instance.CurrentBattle = response;
                SceneManager.LoadScene("BattleScene");
            },
            error => Debug.LogError("Failed to start battle: " + error)
        ));
    }
}
